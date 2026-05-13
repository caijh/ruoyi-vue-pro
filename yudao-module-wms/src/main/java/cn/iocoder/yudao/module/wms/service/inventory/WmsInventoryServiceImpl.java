package cn.iocoder.yudao.module.wms.service.inventory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.WmsInventoryPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDetailDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryHistoryDO;
import cn.iocoder.yudao.module.wms.dal.mysql.inventory.WmsInventoryMapper;
import cn.iocoder.yudao.module.wms.framework.config.WmsProperties;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * WMS 库存 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsInventoryServiceImpl implements WmsInventoryService {

    @Resource
    private WmsInventoryMapper inventoryMapper;

    @Resource
    private WmsInventoryDetailService inventoryDetailService;
    @Resource
    private WmsInventoryHistoryService inventoryHistoryService;

    @Resource
    private WmsProperties wmsProperties;

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

        // 1. 按固定顺序变更库存余额，避免多 SKU/仓库并发操作时交叉拿锁
        Map<WmsInventoryChangeReqDTO.Item, Tuple> resultMap = changeInventoryList(reqDTO.getItems());

        // 2. 构建库存流水与批次库存明细
        boolean batchEnabled = wmsProperties.isBatchEnabled();
        List<WmsInventoryHistoryDO> histories = new ArrayList<>(reqDTO.getItems().size());
        List<WmsInventoryDetailDO> details = batchEnabled ? new ArrayList<>(reqDTO.getItems().size()) : List.of();
        List<WmsInventoryChangeReqDTO.Item> decreaseItems = batchEnabled ? new ArrayList<>(reqDTO.getItems().size()) : List.of();
        for (WmsInventoryChangeReqDTO.Item item : reqDTO.getItems()) {
            histories.add(buildInventoryHistory(reqDTO, item, resultMap.get(item)));
            if (batchEnabled) {
                if (isIncrease(item)) {
                    details.add(buildInventoryDetail(reqDTO, item));
                } else {
                    decreaseItems.add(item);
                }
            }
        }

        // 3. 按固定顺序变更批次库存明细
        if (batchEnabled) {
            inventoryDetailService.decreaseInventoryDetailList(decreaseItems);
            inventoryDetailService.createInventoryDetailList(details);
        }

        // 4. 批量写入库存流水
        inventoryHistoryService.createInventoryHistoryList(histories);
    }

    /**
     * 批量变更库存余额
     *
     * 按 SKU、仓库、库区固定顺序拿锁；相同库存余额只更新一次，避免同一批操作内反复更新。
     *
     * @param items 库存变更明细列表
     * @return 每条变更明细对应的变更前数量、变更后数量
     */
    private Map<WmsInventoryChangeReqDTO.Item, Tuple> changeInventoryList(List<WmsInventoryChangeReqDTO.Item> items) {
        List<WmsInventoryChangeReqDTO.Item> sortedItems = sortByInventoryKey(items);
        Map<WmsInventoryChangeReqDTO.Item, Tuple> resultMap = new IdentityHashMap<>(items.size());
        for (int i = 0; i < sortedItems.size(); ) {
            WmsInventoryChangeReqDTO.Item firstItem = sortedItems.get(i);
            WmsInventoryDO inventory = getOrCreateInventory(firstItem);
            BigDecimal currentQuantity = inventory.getQuantity();
            BigDecimal totalChangeQuantity = BigDecimal.ZERO;

            int j = i;
            while (j < sortedItems.size() && isSameInventoryKey(firstItem, sortedItems.get(j))) {
                WmsInventoryChangeReqDTO.Item item = sortedItems.get(j);
                BigDecimal beforeQuantity = currentQuantity;
                currentQuantity = currentQuantity.add(item.getQuantity());
                if (currentQuantity.compareTo(BigDecimal.ZERO) < 0) {
                    throw exception(INVENTORY_QUANTITY_NOT_ENOUGH, beforeQuantity, item.getQuantity());
                }
                resultMap.put(item, new Tuple(beforeQuantity, currentQuantity));
                totalChangeQuantity = totalChangeQuantity.add(item.getQuantity());
                j++;
            }

            if (totalChangeQuantity.compareTo(BigDecimal.ZERO) != 0) {
                inventoryMapper.updateQuantity(inventory.getId(), totalChangeQuantity);
            }
            i = j;
        }
        return resultMap;
    }

    private WmsInventoryDO getOrCreateInventory(WmsInventoryChangeReqDTO.Item item) {
        // 1. 查询库存余额，使用 FOR UPDATE 锁定行
        WmsInventoryDO inventory = inventoryMapper.selectBySkuIdAndWarehouseIdAndAreaIdForUpdate(item.getSkuId(),
                item.getWarehouseId(), item.getAreaId());
        if (inventory != null) {
            return inventory;
        }
        // 2.1 不存在，则插入一条库存余额记录，初始数量为 0
        try {
            inventory = new WmsInventoryDO().setSkuId(item.getSkuId())
                    .setWarehouseId(item.getWarehouseId()).setAreaId(item.getAreaId()).setQuantity(BigDecimal.ZERO);
            inventoryMapper.insert(inventory);
            return inventory;
        } catch (DuplicateKeyException ex) {
            // 2.2 插入失败，说明有并发冲突，重试查询库存余额
            inventory = inventoryMapper.selectBySkuIdAndWarehouseIdAndAreaIdForUpdate(item.getSkuId(),
                    item.getWarehouseId(), item.getAreaId());
            if (inventory == null) {
                throw ex;
            }
            return inventory;
        }
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

    private WmsInventoryDetailDO buildInventoryDetail(WmsInventoryChangeReqDTO reqDTO,
                                                     WmsInventoryChangeReqDTO.Item item) {
        return new WmsInventoryDetailDO()
                .setSkuId(item.getSkuId()).setWarehouseId(item.getWarehouseId()).setAreaId(item.getAreaId())
                .setQuantity(item.getQuantity()).setRemainQuantity(item.getQuantity())
                .setBatchNo(item.getBatchNo()).setProductionDate(item.getProductionDate())
                .setExpirationDate(item.getExpirationDate())
                .setAmount(item.getAmount()).setRemark(item.getRemark())
                .setOrderId(reqDTO.getOrderId()).setOrderNo(reqDTO.getOrderNo()).setOrderType(reqDTO.getOrderType());
    }

    private boolean isIncrease(WmsInventoryChangeReqDTO.Item item) {
        return item.getQuantity().compareTo(BigDecimal.ZERO) > 0;
    }

    private static List<WmsInventoryChangeReqDTO.Item> sortByInventoryKey(List<WmsInventoryChangeReqDTO.Item> items) {
        return items.stream()
                .sorted(Comparator.comparing(WmsInventoryChangeReqDTO.Item::getSkuId)
                        .thenComparing(WmsInventoryChangeReqDTO.Item::getWarehouseId)
                        .thenComparing(WmsInventoryChangeReqDTO.Item::getAreaId))
                .toList();
    }

    private static boolean isSameInventoryKey(WmsInventoryChangeReqDTO.Item item1,
                                              WmsInventoryChangeReqDTO.Item item2) {
        return item1.getSkuId().equals(item2.getSkuId())
                && item1.getWarehouseId().equals(item2.getWarehouseId())
                && item1.getAreaId().equals(item2.getAreaId());
    }

}
