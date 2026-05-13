package cn.iocoder.yudao.module.wms.service.order.movement;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.wms.controller.admin.order.movement.vo.order.WmsMovementOrderSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.movement.WmsMovementOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.movement.WmsMovementOrderDetailMapper;
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
 * WMS 移库单明细 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsMovementOrderDetailServiceImpl implements WmsMovementOrderDetailService {

    @Resource
    private WmsMovementOrderDetailMapper movementOrderDetailMapper;
    @Resource
    private WmsItemSkuService itemSkuService;
    @Resource
    private WmsWarehouseAreaService warehouseAreaService;
    @Resource
    private WmsProperties wmsProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMovementOrderDetailList(Long orderId, WmsMovementOrderSaveReqVO reqVO) {
        List<WmsMovementOrderDetailDO> list = buildMovementOrderDetailList(reqVO);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        list.forEach(detail -> detail.setId(null).setOrderId(orderId));
        movementOrderDetailMapper.insertBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMovementOrderDetailList(Long orderId, WmsMovementOrderSaveReqVO reqVO) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<WmsMovementOrderDetailDO> oldList = movementOrderDetailMapper.selectListByOrderId(orderId);
        List<WmsMovementOrderDetailDO> list = buildMovementOrderDetailList(reqVO);
        List<WmsMovementOrderDetailDO> newList = CollUtil.isEmpty(list) ? ListUtil.of() : list;
        List<List<WmsMovementOrderDetailDO>> diffList = diffList(oldList, newList, // id 不同，就认为是不同的记录
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            if (CollUtil.isNotEmpty(convertList(diffList.get(0), WmsMovementOrderDetailDO::getId))) {
                throw exception(MOVEMENT_ORDER_DETAIL_NOT_EXISTS);
            }
            diffList.get(0).forEach(detail -> detail.setOrderId(orderId));
            movementOrderDetailMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            diffList.get(1).forEach(detail -> detail.setOrderId(orderId));
            movementOrderDetailMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            movementOrderDetailMapper.deleteByIds(convertList(diffList.get(2), WmsMovementOrderDetailDO::getId));
        }
    }

    @Override
    public void deleteMovementOrderDetailListByOrderId(Long orderId) {
        movementOrderDetailMapper.deleteByOrderId(orderId);
    }

    @Override
    public List<WmsMovementOrderDetailDO> getMovementOrderDetailList(Long orderId) {
        return movementOrderDetailMapper.selectListByOrderId(orderId);
    }

    @Override
    public List<WmsMovementOrderDetailDO> getMovementOrderDetailList(Collection<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return ListUtil.of();
        }
        return movementOrderDetailMapper.selectListByOrderIds(orderIds);
    }

    @Override
    public List<WmsMovementOrderDetailDO> validateMovementOrderDetailListExists(Long orderId) {
        List<WmsMovementOrderDetailDO> details = movementOrderDetailMapper.selectListByOrderId(orderId);
        if (CollUtil.isEmpty(details)) {
            throw exception(MOVEMENT_ORDER_DETAIL_REQUIRED);
        }
        if (wmsProperties.isAreaEnabled() && details.stream().anyMatch(detail ->
                ObjectUtils.equalsAny(detail.getSourceAreaId(), null, WmsWarehouseAreaDO.ID_EMPTY)
                        || ObjectUtils.equalsAny(detail.getTargetAreaId(), null, WmsWarehouseAreaDO.ID_EMPTY))) {
            throw exception(MOVEMENT_ORDER_AREA_REQUIRED);
        }
        return details;
    }

    private List<WmsMovementOrderDetailDO> buildMovementOrderDetailList(WmsMovementOrderSaveReqVO reqVO) {
        if (CollUtil.isEmpty(reqVO.getDetails())) {
            return ListUtil.of();
        }
        return convertList(reqVO.getDetails(), detail -> {
            // 校验 SKU 存在
            itemSkuService.validateItemSkuExists(detail.getSkuId());
            // 校验来源、目标库区存在，并且属于对应仓库
            Long sourceAreaId = warehouseAreaService.validateAndNormalizeWarehouseAreaId(
                    detail.getSourceAreaId() == null ? reqVO.getSourceAreaId() : detail.getSourceAreaId(),
                    reqVO.getSourceWarehouseId());
            Long targetAreaId = warehouseAreaService.validateAndNormalizeWarehouseAreaId(
                    detail.getTargetAreaId() == null ? reqVO.getTargetAreaId() : detail.getTargetAreaId(),
                    reqVO.getTargetWarehouseId());
            // 构建对象
            return BeanUtils.toBean(detail, WmsMovementOrderDetailDO.class)
                    .setSourceWarehouseId(reqVO.getSourceWarehouseId()).setSourceAreaId(sourceAreaId)
                    .setTargetWarehouseId(reqVO.getTargetWarehouseId()).setTargetAreaId(targetAreaId);
        });
    }

}
