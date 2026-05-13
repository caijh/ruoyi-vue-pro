package cn.iocoder.yudao.module.wms.service.order.receipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.wms.controller.admin.order.receipt.vo.order.WmsReceiptOrderSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt.WmsReceiptOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.receipt.WmsReceiptOrderDetailMapper;
import cn.iocoder.yudao.module.wms.framework.config.WmsProperties;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemSkuService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseAreaService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * WMS 入库单明细 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsReceiptOrderDetailServiceImpl implements WmsReceiptOrderDetailService {

    @Resource
    private WmsReceiptOrderDetailMapper receiptOrderDetailMapper;
    @Resource
    private WmsItemSkuService itemSkuService;
    @Resource
    private WmsWarehouseAreaService warehouseAreaService;
    @Resource
    private WmsProperties wmsProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReceiptOrderDetailList(Long orderId, WmsReceiptOrderSaveReqVO reqVO) {
        List<WmsReceiptOrderDetailDO> list = buildReceiptOrderDetailList(reqVO);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.forEach(detail -> detail.setId(null).setOrderId(orderId));
        receiptOrderDetailMapper.insertBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReceiptOrderDetailList(Long orderId, WmsReceiptOrderSaveReqVO reqVO) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<WmsReceiptOrderDetailDO> oldList = receiptOrderDetailMapper.selectListByOrderId(orderId);
        List<WmsReceiptOrderDetailDO> list = buildReceiptOrderDetailList(reqVO);
        List<WmsReceiptOrderDetailDO> newList = CollUtil.isEmpty(list) ? ListUtil.of() : list;
        List<List<WmsReceiptOrderDetailDO>> diffList = diffList(oldList, newList, // id 不同，就认为是不同的记录
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            if (CollUtil.isNotEmpty(convertList(diffList.get(0), WmsReceiptOrderDetailDO::getId))) {
                throw exception(RECEIPT_ORDER_DETAIL_NOT_EXISTS);
            }
            diffList.get(0).forEach(detail -> detail.setOrderId(orderId));
            receiptOrderDetailMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            diffList.get(1).forEach(detail -> detail.setOrderId(orderId));
            receiptOrderDetailMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            receiptOrderDetailMapper.deleteByIds(convertList(diffList.get(2), WmsReceiptOrderDetailDO::getId));
        }
    }

    @Override
    public void deleteReceiptOrderDetailListByOrderId(Long orderId) {
        receiptOrderDetailMapper.deleteByOrderId(orderId);
    }

    @Override
    public List<WmsReceiptOrderDetailDO> getReceiptOrderDetailList(Long orderId) {
        return receiptOrderDetailMapper.selectListByOrderId(orderId);
    }

    @Override
    public List<WmsReceiptOrderDetailDO> getReceiptOrderDetailList(Collection<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return ListUtil.of();
        }
        return receiptOrderDetailMapper.selectListByOrderIds(orderIds);
    }

    @Override
    public List<WmsReceiptOrderDetailDO> validateReceiptOrderDetailListExists(Long orderId) {
        List<WmsReceiptOrderDetailDO> details = receiptOrderDetailMapper.selectListByOrderId(orderId);
        if (CollUtil.isEmpty(details)) {
            throw exception(RECEIPT_ORDER_DETAIL_REQUIRED);
        }
        if (wmsProperties.isAreaEnabled() && details.stream()
                .anyMatch(detail -> ObjectUtils.equalsAny(detail.getAreaId(), null, WmsWarehouseAreaDO.ID_EMPTY))) {
            throw exception(RECEIPT_ORDER_AREA_REQUIRED);
        }
        return details;
    }

    @Override
    public long getReceiptOrderDetailCountBySkuId(Long skuId) {
        return receiptOrderDetailMapper.selectCountBySkuId(skuId);
    }

    @Override
    public long getReceiptOrderDetailCountByAreaId(Long areaId) {
        return receiptOrderDetailMapper.selectCountByAreaId(areaId);
    }

    private List<WmsReceiptOrderDetailDO> buildReceiptOrderDetailList(WmsReceiptOrderSaveReqVO reqVO) {
        if (CollUtil.isEmpty(reqVO.getDetails())) {
            return ListUtil.of();
        }
        return convertList(reqVO.getDetails(), detail -> {
            // 校验 SKU 存在
            itemSkuService.validateItemSkuExists(detail.getSkuId());
            // 校验仓库区域存在，并且属于当前仓库
            Long areaId = warehouseAreaService.validateAndNormalizeWarehouseAreaId(
                    detail.getAreaId() == null ? reqVO.getAreaId() : detail.getAreaId(), reqVO.getWarehouseId());
            // 构建对象
            return BeanUtils.toBean(detail, WmsReceiptOrderDetailDO.class)
                    .setWarehouseId(reqVO.getWarehouseId()).setAreaId(areaId);
        });
    }

}
