package cn.iocoder.yudao.module.wms.service.inventory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.WmsInventoryPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryHistoryDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import cn.iocoder.yudao.module.wms.dal.mysql.inventory.WmsInventoryMapper;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemService;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemSkuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * WMS 库存 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class WmsInventoryServiceImpl implements WmsInventoryService {

    @Resource
    private WmsInventoryMapper inventoryMapper;

    @Resource
    private WmsInventoryHistoryService inventoryHistoryService;

    @Resource
    private WmsItemSkuService itemSkuService;
    @Resource
    private WmsItemService itemService;

    @Override
    public PageResult<WmsInventoryDO> getInventoryPage(WmsInventoryPageReqVO pageReqVO) {
        if (StrUtil.equals(WmsInventoryPageReqVO.TYPE_WAREHOUSE, pageReqVO.getType())) {
            return inventoryMapper.selectPageGroupByWarehouse(pageReqVO);
        }
        return inventoryMapper.selectPage(pageReqVO);
    }

    @Override
    public long getInventoryCountBySkuId(Long skuId) {
        return inventoryMapper.selectCountBySkuId(skuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeInventory(WmsInventoryChangeReqDTO reqDTO) {
        if (reqDTO == null || CollUtil.isEmpty(reqDTO.getItems())) {
            return;
        }

        // 1. 补齐并锁定本次涉及的库存余额行，再计算库存变更
        Map<WmsInventoryChangeReqDTO.Item, Tuple> resultMap = changeInventoryList(reqDTO.getItems());

        // 2. 批量写入库存流水
        List<WmsInventoryHistoryDO> histories = new ArrayList<>(reqDTO.getItems().size());
        for (WmsInventoryChangeReqDTO.Item item : reqDTO.getItems()) {
            histories.add(buildInventoryHistory(reqDTO, item, resultMap.get(item)));
        }
        inventoryHistoryService.createInventoryHistoryList(histories);
    }

    /**
     * 批量变更库存余额
     *
     * 1. 库存行按 ID 批量加锁
     * 2. 库存变更先在内存计算并校验，全部通过后批量覆盖库存数量。
     *
     * @param items 库存变更明细列表
     * @return 每条变更明细对应的变更前数量、变更后数量
     */
    private Map<WmsInventoryChangeReqDTO.Item, Tuple> changeInventoryList(List<WmsInventoryChangeReqDTO.Item> items) {
        // 1.1 创建或锁定库存行
        List<WmsInventoryDO> inventories = getOrCreateInventoryList(items);
        // 1.2 锁定库存：避免 quantity 变更时的并发问题，导致 quantity 前后计算不对
        inventories = inventoryMapper.selectListByIdsForUpdate(convertSet(inventories, WmsInventoryDO::getId));

        // 2.1 校验库存充足
        Map<WmsInventoryChangeReqDTO.Item, Tuple> resultMap = new IdentityHashMap<>(items.size());
        for (WmsInventoryChangeReqDTO.Item item : items) {
            WmsInventoryDO inventory = findInventory(inventories, item);
            if (inventory == null) {
                throw new IllegalStateException("库存行不存在，skuId=" + item.getSkuId()
                        + ", warehouseId=" + item.getWarehouseId() + ", areaId=" + item.getAreaId());
            }
            BigDecimal beforeQuantity = inventory.getQuantity();
            BigDecimal afterQuantity = beforeQuantity.add(item.getQuantity());
            if (afterQuantity.compareTo(BigDecimal.ZERO) < 0) {
                throw buildInventoryQuantityNotEnoughException(item, beforeQuantity);
            }
            inventory.setQuantity(afterQuantity);
            resultMap.put(item, new Tuple(beforeQuantity, afterQuantity));
        }
        // 2.2 批量更新库存数量（加锁安全）
        if (CollUtil.isNotEmpty(inventories)) {
            inventoryMapper.updateBatch(convertList(inventories, inventory ->
                    new WmsInventoryDO().setId(inventory.getId()).setQuantity(inventory.getQuantity())));
        }
        return resultMap;
    }

    private List<WmsInventoryDO> getOrCreateInventoryList(List<WmsInventoryChangeReqDTO.Item> items) {
        // 先按库存维度在内存去重，避免同一批明细重复查询或重复补行。
        List<WmsInventoryDO> inventoryKeyList = new ArrayList<>(items.size());
        for (WmsInventoryChangeReqDTO.Item item : items) {
            if (findInventory(inventoryKeyList, item) == null) {
                inventoryKeyList.add(new WmsInventoryDO().setSkuId(item.getSkuId())
                        .setWarehouseId(item.getWarehouseId()).setAreaId(item.getAreaId()));
            }
        }

        // 批量查询已存在的库存行；这里不加锁，后续统一按库存 ID 批量加锁。
        List<WmsInventoryDO> inventories = inventoryMapper.selectListByKeys(inventoryKeyList);

        // 对比库存维度，找出数据库中还不存在的库存行，再按唯一索引兜底补齐。
        List<WmsInventoryDO> missingInventoryList = new ArrayList<>(inventoryKeyList.size());
        for (WmsInventoryDO inventoryKey : inventoryKeyList) {
            if (findInventory(inventories, inventoryKey) == null) {
                missingInventoryList.add(inventoryKey);
            }
        }
        if (CollUtil.isEmpty(missingInventoryList)) {
            return inventories;
        }

        inventories.addAll(createMissingInventoryList(missingInventoryList));
        return inventories;
    }

    private List<WmsInventoryDO> createMissingInventoryList(List<WmsInventoryDO> missingInventoryList) {
        // 优先批量插入缺失库存行；并发唯一键冲突时，再逐个插入并回查冲突行。
        List<WmsInventoryDO> newInventoryList = convertList(missingInventoryList, missingInventory ->
                new WmsInventoryDO().setSkuId(missingInventory.getSkuId()).setWarehouseId(missingInventory.getWarehouseId())
                        .setAreaId(missingInventory.getAreaId()).setQuantity(BigDecimal.ZERO));
        try {
            inventoryMapper.insertBatch(newInventoryList);
            return newInventoryList;
        } catch (DuplicateKeyException ex) {
            // 并发事务可能已经补齐部分库存行，降级为逐个插入并回查唯一键冲突的行。
            log.warn("[createMissingInventoryList][missingInventoryList({}) 批量插入库存行冲突，降级为逐个插入]",
                    missingInventoryList, ex);
        }

        // 批量插入失败后逐条补齐；单条唯一键冲突时，回查并使用并发事务创建的库存行。
        List<WmsInventoryDO> resultList = new ArrayList<>(missingInventoryList.size());
        for (WmsInventoryDO missingInventory : missingInventoryList) {
            WmsInventoryDO inventory = new WmsInventoryDO().setSkuId(missingInventory.getSkuId())
                    .setWarehouseId(missingInventory.getWarehouseId()).setAreaId(missingInventory.getAreaId())
                    .setQuantity(BigDecimal.ZERO);
            try {
                inventoryMapper.insert(inventory);
            } catch (DuplicateKeyException ex) {
                inventory = inventoryMapper.selectBySkuIdAndWarehouseIdAndAreaId(
                        missingInventory.getSkuId(), missingInventory.getWarehouseId(), missingInventory.getAreaId());
                log.warn("[createMissingInventoryList][missingInventory({}) 插入库存行冲突，回查已有库存行]", missingInventory, ex);
                if (inventory == null) {
                    throw ex;
                }
            }
            resultList.add(inventory);
        }
        return resultList;
    }

    private WmsInventoryHistoryDO buildInventoryHistory(WmsInventoryChangeReqDTO reqDTO,
                                                       WmsInventoryChangeReqDTO.Item item,
                                                       Tuple result) {
        return new WmsInventoryHistoryDO()
                .setWarehouseId(item.getWarehouseId()).setAreaId(item.getAreaId()).setSkuId(item.getSkuId())
                .setQuantity(item.getQuantity()).setBeforeQuantity(result.get(0)).setAfterQuantity(result.get(1))
                .setBatchNo(item.getBatchNo()).setProductionDate(item.getProductionDate())
                .setExpirationDate(item.getExpirationDate())
                .setAmount(item.getAmount()).setRemark(item.getRemark())
                .setOrderId(reqDTO.getOrderId()).setOrderNo(reqDTO.getOrderNo()).setOrderType(reqDTO.getOrderType());
    }

    private static WmsInventoryDO findInventory(List<WmsInventoryDO> inventories, WmsInventoryChangeReqDTO.Item item) {
        return CollUtil.findOne(inventories, inventory -> isSameInventory(inventory, item));
    }

    private static WmsInventoryDO findInventory(List<WmsInventoryDO> inventories, WmsInventoryDO key) {
        return CollUtil.findOne(inventories, inventory -> isSameInventory(inventory, key));
    }

    private static boolean isSameInventory(WmsInventoryDO inventory, WmsInventoryChangeReqDTO.Item item) {
        return ObjectUtil.equal(inventory.getSkuId(), item.getSkuId())
                && ObjectUtil.equal(inventory.getWarehouseId(), item.getWarehouseId())
                && ObjectUtil.equal(inventory.getAreaId(), item.getAreaId());
    }

    private static boolean isSameInventory(WmsInventoryDO inventory, WmsInventoryDO key) {
        return ObjectUtil.equal(inventory.getSkuId(), key.getSkuId())
                && ObjectUtil.equal(inventory.getWarehouseId(), key.getWarehouseId())
                && ObjectUtil.equal(inventory.getAreaId(), key.getAreaId());
    }

    private ServiceException buildInventoryQuantityNotEnoughException(WmsInventoryChangeReqDTO.Item item,
                                                                      BigDecimal beforeQuantity) {
        WmsItemSkuDO skuDO = itemSkuService.validateItemSkuExists(item.getSkuId());
        WmsItemDO itemDO = itemService.validateItemExists(skuDO.getItemId());
        return exception(INVENTORY_QUANTITY_NOT_ENOUGH, itemDO.getName(), skuDO.getName(),
                item.getWarehouseId(), item.getAreaId(), beforeQuantity, item.getQuantity());
    }

}
