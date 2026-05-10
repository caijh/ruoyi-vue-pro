package cn.iocoder.yudao.module.wms.dal.mysql.md.warehouse;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.wms.controller.admin.md.warehouse.vo.area.WmsWarehouseAreaPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * WMS 库区 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface WmsWarehouseAreaMapper extends BaseMapperX<WmsWarehouseAreaDO> {

    default PageResult<WmsWarehouseAreaDO> selectPage(WmsWarehouseAreaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WmsWarehouseAreaDO>()
                .eqIfPresent(WmsWarehouseAreaDO::getCode, reqVO.getCode())
                .likeIfPresent(WmsWarehouseAreaDO::getName, reqVO.getName())
                .eqIfPresent(WmsWarehouseAreaDO::getWarehouseId, reqVO.getWarehouseId())
                .orderByDesc(WmsWarehouseAreaDO::getId));
    }

    default WmsWarehouseAreaDO selectByCode(String code) {
        return selectOne(WmsWarehouseAreaDO::getCode, code);
    }

    default WmsWarehouseAreaDO selectByName(Long warehouseId, String name) {
        return selectOne(new LambdaQueryWrapperX<WmsWarehouseAreaDO>()
                .eq(WmsWarehouseAreaDO::getWarehouseId, warehouseId)
                .eq(WmsWarehouseAreaDO::getName, name));
    }

    default List<WmsWarehouseAreaDO> selectListByWarehouseId(Long warehouseId) {
        return selectList(new LambdaQueryWrapperX<WmsWarehouseAreaDO>()
                .eqIfPresent(WmsWarehouseAreaDO::getWarehouseId, warehouseId)
                .orderByDesc(WmsWarehouseAreaDO::getId));
    }

    default Long selectCountByWarehouseId(Long warehouseId) {
        return selectCount(WmsWarehouseAreaDO::getWarehouseId, warehouseId);
    }

}
