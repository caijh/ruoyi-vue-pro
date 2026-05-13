package cn.iocoder.yudao.module.wms.dal.mysql.order.shipment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.wms.controller.admin.order.shipment.vo.order.WmsShipmentOrderPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.shipment.WmsShipmentOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * WMS 出库单 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface WmsShipmentOrderMapper extends BaseMapperX<WmsShipmentOrderDO> {

    default PageResult<WmsShipmentOrderDO> selectPage(WmsShipmentOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WmsShipmentOrderDO>()
                .likeIfPresent(WmsShipmentOrderDO::getNo, reqVO.getNo())
                .eqIfPresent(WmsShipmentOrderDO::getStatus, reqVO.getStatus())
                .eqIfPresent(WmsShipmentOrderDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(WmsShipmentOrderDO::getMerchantId, reqVO.getMerchantId())
                .betweenIfPresent(WmsShipmentOrderDO::getCreateTime, reqVO.getOrderDate())
                .geIfPresent(WmsShipmentOrderDO::getTotalQuantity, reqVO.getTotalQuantityMin())
                .leIfPresent(WmsShipmentOrderDO::getTotalQuantity, reqVO.getTotalQuantityMax())
                .geIfPresent(WmsShipmentOrderDO::getTotalAmount, reqVO.getTotalAmountMin())
                .leIfPresent(WmsShipmentOrderDO::getTotalAmount, reqVO.getTotalAmountMax())
                .eqIfPresent(WmsShipmentOrderDO::getType, reqVO.getType())
                .likeIfPresent(WmsShipmentOrderDO::getBizOrderNo, reqVO.getBizOrderNo())
                .eqIfPresent(WmsShipmentOrderDO::getCreator, reqVO.getCreator())
                .eqIfPresent(WmsShipmentOrderDO::getUpdater, reqVO.getUpdater())
                .betweenIfPresent(WmsShipmentOrderDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(WmsShipmentOrderDO::getUpdateTime, reqVO.getUpdateTime())
                .orderByDesc(WmsShipmentOrderDO::getId));
    }

    default WmsShipmentOrderDO selectByNo(String no) {
        return selectOne(WmsShipmentOrderDO::getNo, no);
    }

    default Long selectCountByMerchantId(Long merchantId) {
        return selectCount(WmsShipmentOrderDO::getMerchantId, merchantId);
    }

    default Long selectCountByWarehouseId(Long warehouseId) {
        return selectCount(WmsShipmentOrderDO::getWarehouseId, warehouseId);
    }

    default Long selectCountByAreaId(Long areaId) {
        return selectCount(WmsShipmentOrderDO::getAreaId, areaId);
    }

}
