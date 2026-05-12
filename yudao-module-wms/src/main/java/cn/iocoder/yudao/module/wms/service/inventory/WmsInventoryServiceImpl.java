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
import java.util.List;

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

        // 1. 变更库存余额，并构建库存流水与批次库存明细
        boolean batchEnabled = wmsProperties.isBatchEnabled();
        List<WmsInventoryHistoryDO> histories = new ArrayList<>(reqDTO.getItems().size());
        List<WmsInventoryDetailDO> details = batchEnabled ? new ArrayList<>(reqDTO.getItems().size()) : List.of();
        for (WmsInventoryChangeReqDTO.Item item : reqDTO.getItems()) {
            // 1.1 变更库存余额
            Tuple result = changeInventory0(item);
            // 1.2 构建库存流水与批次库存明细
            histories.add(buildInventoryHistory(reqDTO, item, result));
            if (batchEnabled) {
                details.add(buildInventoryDetail(reqDTO, item));
            }
        }

        // 2.1 批量写入库存流水
        inventoryHistoryService.createInventoryHistoryList(histories);
        if (batchEnabled) {
            // 2.2 批量写入批次库存明细
            inventoryDetailService.createInventoryDetailList(details);
        }
    }

    /**
     * 变更库存余额
     *
     * @param item 库存变更明细
     * @return 变更前数量、变更后数量
     */
    private Tuple changeInventory0(WmsInventoryChangeReqDTO.Item item) {
        // 1. 获取或创建库存余额，使用 FOR UPDATE 锁定行
        WmsInventoryDO inventory = getOrCreateInventory(item);
        // 2. 变更库存余额
        BigDecimal beforeQuantity = inventory.getQuantity();
        BigDecimal afterQuantity = beforeQuantity.add(item.getQuantity());
        inventoryMapper.updateQuantity(inventory.getId(), item.getQuantity());
        return new Tuple(beforeQuantity, afterQuantity);
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

}
