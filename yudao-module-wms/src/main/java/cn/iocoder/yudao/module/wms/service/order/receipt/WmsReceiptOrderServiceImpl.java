package cn.iocoder.yudao.module.wms.service.order.receipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.wms.controller.admin.order.receipt.vo.order.WmsReceiptOrderPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.order.receipt.vo.order.WmsReceiptOrderSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.merchant.WmsMerchantDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt.WmsReceiptOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt.WmsReceiptOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.receipt.WmsReceiptOrderMapper;
import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import cn.iocoder.yudao.module.wms.enums.md.WmsMerchantTypeEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsReceiptOrderStatusEnum;
import cn.iocoder.yudao.module.wms.framework.config.WmsProperties;
import cn.iocoder.yudao.module.wms.service.inventory.WmsInventoryService;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemSkuService;
import cn.iocoder.yudao.module.wms.service.md.merchant.WmsMerchantService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseAreaService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * WMS 入库单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsReceiptOrderServiceImpl implements WmsReceiptOrderService {

    private static final Long EMPTY_AREA_ID = 0L;

    @Resource
    private WmsReceiptOrderMapper receiptOrderMapper;
    @Resource
    private WmsReceiptOrderDetailService receiptOrderDetailService;
    @Resource
    private WmsWarehouseService warehouseService;
    @Resource
    private WmsWarehouseAreaService warehouseAreaService;
    @Resource
    private WmsMerchantService merchantService;
    @Resource
    private WmsItemSkuService itemSkuService;
    @Resource
    private WmsInventoryService inventoryService;
    @Resource
    private WmsProperties wmsProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReceiptOrder(WmsReceiptOrderSaveReqVO createReqVO) {
        return createReceiptOrder(createReqVO, false).getId();
    }

    // TODO @AI：不需要 detailRequired 参数呀。。。（对齐 mes 的做法）
    private WmsReceiptOrderDO createReceiptOrder(WmsReceiptOrderSaveReqVO createReqVO, boolean detailRequired) {
        // 1. 校验入库单保存数据
        validateReceiptOrderSaveData(createReqVO);
        // TODO @AI：是不是（对齐 mes 的做法）
        List<WmsReceiptOrderDetailDO> details = buildReceiptOrderDetailList(createReqVO, detailRequired);

        // 2.1 插入入库单
        WmsReceiptOrderDO order = BeanUtils.toBean(createReqVO, WmsReceiptOrderDO.class);
        order.setStatus(WmsReceiptOrderStatusEnum.PENDING.getStatus());
        fillReceiptOrderTotal(order, details);
        receiptOrderMapper.insert(order);
        // 2.2 插入入库单明细
        receiptOrderDetailService.createReceiptOrderDetailList(order.getId(), details);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReceiptOrder(WmsReceiptOrderSaveReqVO updateReqVO) {
        updateReceiptOrder(updateReqVO, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReceiptOrder(Long id) {
        // 1. 校验存在，且暂存
        WmsReceiptOrderDO order = validateReceiptOrderExists(id);
        // TODO @AI：只有未“COMPLETED”不允许删除。
        validateReceiptOrderPending(order);

        // 2.1 删除入库单
        receiptOrderMapper.deleteById(id);
        // 2.1 删除入库单明细
        receiptOrderDetailService.deleteReceiptOrderDetailListByOrderId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeReceiptOrder(Long id) {
        // 1.1 校验存在，且未完成
        WmsReceiptOrderDO order = validateReceiptOrderExists(id);
        validateReceiptOrderPending(order);
        // 1.2 校验入库单明细存在
        List<WmsReceiptOrderDetailDO> details = validateReceiptOrderDetailExists(id);

        // 2.1 完成入库单
        WmsReceiptOrderDO updateObj = new WmsReceiptOrderDO().setId(id).setStatus(WmsReceiptOrderStatusEnum.COMPLETED.getStatus());
        receiptOrderMapper.updateById(updateObj);
        // 2.2 写入库存
        createInventory(order, details);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReceiptOrder(Long id) {
        // 1. 校验存在，且未完成
        WmsReceiptOrderDO order = validateReceiptOrderExists(id);
        validateReceiptOrderPending(order);

        // 2. 作废入库单
        receiptOrderMapper.updateById(new WmsReceiptOrderDO().setId(id)
                .setStatus(WmsReceiptOrderStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public WmsReceiptOrderDO getReceiptOrder(Long id) {
        return receiptOrderMapper.selectById(id);
    }

    @Override
    public PageResult<WmsReceiptOrderDO> getReceiptOrderPage(WmsReceiptOrderPageReqVO pageReqVO) {
        return receiptOrderMapper.selectPage(pageReqVO);
    }

    private WmsReceiptOrderDO updateReceiptOrder(WmsReceiptOrderSaveReqVO updateReqVO, boolean detailRequired) {
        // 1. 校验入库单保存数据
        WmsReceiptOrderDO oldOrder = validateReceiptOrderExists(updateReqVO.getId());
        validateReceiptOrderPending(oldOrder);
        validateReceiptOrderSaveData(updateReqVO);
        List<WmsReceiptOrderDetailDO> details = buildReceiptOrderDetailList(updateReqVO, detailRequired);

        // 2.1 更新入库单
        WmsReceiptOrderDO updateObj = BeanUtils.toBean(updateReqVO, WmsReceiptOrderDO.class)
                .setStatus(WmsReceiptOrderStatusEnum.PENDING.getStatus());
        fillReceiptOrderTotal(updateObj, details);
        receiptOrderMapper.updateById(updateObj);
        // 2.2 更新入库单明细
        receiptOrderDetailService.updateReceiptOrderDetailList(updateReqVO.getId(), details);
        return updateObj;
    }

    private void validateReceiptOrderSaveData(WmsReceiptOrderSaveReqVO reqVO) {
        validateReceiptOrderNoUnique(reqVO.getId(), reqVO.getNo());
        warehouseService.validateWarehouseExists(reqVO.getWarehouseId());
        validateReceiptOrderMerchant(reqVO.getMerchantId());
        reqVO.setAreaId(normalizeAreaId(reqVO.getAreaId(), reqVO.getWarehouseId(), false));
    }

    private void validateReceiptOrderNoUnique(Long id, String no) {
        WmsReceiptOrderDO order = receiptOrderMapper.selectByNo(no);
        if (order == null) {
            return;
        }
        if (id == null || ObjectUtil.notEqual(order.getId(), id)) {
            throw exception(RECEIPT_ORDER_NO_DUPLICATE);
        }
    }

    private WmsReceiptOrderDO validateReceiptOrderExists(Long id) {
        WmsReceiptOrderDO order = id == null ? null : receiptOrderMapper.selectById(id);
        if (order == null) {
            throw exception(RECEIPT_ORDER_NOT_EXISTS);
        }
        return order;
    }

    private void validateReceiptOrderPending(WmsReceiptOrderDO order) {
        if (ObjectUtil.notEqual(order.getStatus(), WmsReceiptOrderStatusEnum.PENDING.getStatus())) {
            throw exception(RECEIPT_ORDER_STATUS_NOT_PENDING);
        }
    }

    private List<WmsReceiptOrderDetailDO> validateReceiptOrderDetailExists(Long id) {
        List<WmsReceiptOrderDetailDO> details = receiptOrderDetailService.getReceiptOrderDetailList(id);
        if (CollUtil.isEmpty(details)) {
            throw exception(RECEIPT_ORDER_DETAIL_REQUIRED);
        }
        return details;
    }

    private void validateReceiptOrderMerchant(Long merchantId) {
        if (merchantId == null) {
            return;
        }
        WmsMerchantDO merchant = merchantService.validateMerchantExists(merchantId);
        if (ObjectUtil.notEqual(merchant.getType(), WmsMerchantTypeEnum.SUPPLIER.getType())
                && ObjectUtil.notEqual(merchant.getType(), WmsMerchantTypeEnum.CUSTOMER_SUPPLIER.getType())) {
            throw exception(RECEIPT_ORDER_MERCHANT_NOT_SUPPLIER);
        }
    }

    private List<WmsReceiptOrderDetailDO> buildReceiptOrderDetailList(WmsReceiptOrderSaveReqVO reqVO, boolean required) {
        if (CollUtil.isEmpty(reqVO.getDetails())) {
            if (required) {
                throw exception(RECEIPT_ORDER_DETAIL_REQUIRED);
            }
            return ListUtil.of();
        }
        return convertList(reqVO.getDetails(), detail -> {
            itemSkuService.validateItemSkuExists(detail.getSkuId());
            WmsReceiptOrderDetailDO detailDO = BeanUtils.toBean(detail, WmsReceiptOrderDetailDO.class);
            detailDO.setWarehouseId(reqVO.getWarehouseId());
            detailDO.setAreaId(normalizeAreaId(detail.getAreaId() == null ? reqVO.getAreaId() : detail.getAreaId(),
                    reqVO.getWarehouseId(), required));
            return detailDO;
        });
    }

    private Long normalizeAreaId(Long areaId, Long warehouseId, boolean required) {
        if (!wmsProperties.isAreaEnabled()) {
            return EMPTY_AREA_ID;
        }
        if (areaId == null || ObjectUtil.equal(areaId, EMPTY_AREA_ID)) {
            if (required) {
                throw exception(RECEIPT_ORDER_AREA_REQUIRED);
            }
            return EMPTY_AREA_ID;
        }
        WmsWarehouseAreaDO area = warehouseAreaService.validateWarehouseAreaExists(areaId);
        if (ObjectUtil.notEqual(area.getWarehouseId(), warehouseId)) {
            throw exception(RECEIPT_ORDER_AREA_NOT_MATCH_WAREHOUSE);
        }
        return areaId;
    }

    // DONE @AI：保留手工总金额优先；未填写时按明细金额汇总回填。
    // TODO @AI：前端自动计算。
    private void fillReceiptOrderTotal(WmsReceiptOrderDO order, List<WmsReceiptOrderDetailDO> details) {
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsReceiptOrderDetailDO detail : details) {
            totalQuantity = totalQuantity.add(detail.getQuantity());
            totalAmount = totalAmount.add(detail.getAmount() == null ? BigDecimal.ZERO : detail.getAmount());
        }
        order.setTotalQuantity(totalQuantity);
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(totalAmount);
        }
    }

    private void createInventory(WmsReceiptOrderDO order, List<WmsReceiptOrderDetailDO> details) {
        List<WmsInventoryChangeReqDTO.Item> items = convertList(details, detail -> new WmsInventoryChangeReqDTO.Item()
                .setSkuId(detail.getSkuId()).setWarehouseId(detail.getWarehouseId()).setAreaId(detail.getAreaId())
                .setQuantity(detail.getQuantity()).setBatchNo(detail.getBatchNo())
                .setProductionDate(detail.getProductionDate()).setExpirationDate(detail.getExpirationDate())
                .setAmount(detail.getAmount()).setRemark(detail.getRemark()));
        inventoryService.changeInventory(new WmsInventoryChangeReqDTO()
                .setOrderId(order.getId()).setOrderNo(order.getNo())
                .setOrderType(WmsInventoryOrderTypeEnum.RECEIPT.getType()).setItems(items));
    }

}
