package cn.iocoder.yudao.module.wms.dal.mysql.order.check;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.wms.controller.admin.order.check.vo.order.WmsCheckOrderPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.check.WmsCheckOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * WMS 盘库单 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface WmsCheckOrderMapper extends BaseMapperX<WmsCheckOrderDO> {

    default PageResult<WmsCheckOrderDO> selectPage(WmsCheckOrderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WmsCheckOrderDO>()
                .likeIfPresent(WmsCheckOrderDO::getNo, reqVO.getNo())
                .eqIfPresent(WmsCheckOrderDO::getStatus, reqVO.getStatus())
                .eqIfPresent(WmsCheckOrderDO::getWarehouseId, reqVO.getWarehouseId())
                .betweenIfPresent(WmsCheckOrderDO::getCreateTime, reqVO.getOrderDate())
                .geIfPresent(WmsCheckOrderDO::getTotalQuantity, reqVO.getTotalQuantityMin())
                .leIfPresent(WmsCheckOrderDO::getTotalQuantity, reqVO.getTotalQuantityMax())
                .geIfPresent(WmsCheckOrderDO::getTotalAmount, reqVO.getTotalAmountMin())
                .leIfPresent(WmsCheckOrderDO::getTotalAmount, reqVO.getTotalAmountMax())
                .eqIfPresent(WmsCheckOrderDO::getCreator, reqVO.getCreator())
                .eqIfPresent(WmsCheckOrderDO::getUpdater, reqVO.getUpdater())
                .betweenIfPresent(WmsCheckOrderDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(WmsCheckOrderDO::getUpdateTime, reqVO.getUpdateTime())
                .orderByDesc(WmsCheckOrderDO::getId));
    }

    default WmsCheckOrderDO selectByNo(String no) {
        return selectOne(WmsCheckOrderDO::getNo, no);
    }

    default Long selectCountByWarehouseId(Long warehouseId) {
        return selectCount(WmsCheckOrderDO::getWarehouseId, warehouseId);
    }

}
