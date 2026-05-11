package cn.iocoder.yudao.module.wms.dal.mysql.inventory;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.WmsInventoryPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * WMS 库存 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface WmsInventoryMapper extends BaseMapperX<WmsInventoryDO> {

    default PageResult<WmsInventoryDO> selectPage(WmsInventoryPageReqVO reqVO) {
        MPJLambdaWrapperX<WmsInventoryDO> query = new MPJLambdaWrapperX<WmsInventoryDO>()
                .selectAll(WmsInventoryDO.class)
                .innerJoin(WmsItemSkuDO.class, WmsItemSkuDO::getId, WmsInventoryDO::getSkuId)
                .innerJoin(WmsItemDO.class, WmsItemDO::getId, WmsItemSkuDO::getItemId)
                .likeIfPresent(WmsItemDO::getCode, reqVO.getItemCode())
                .likeIfPresent(WmsItemDO::getName, reqVO.getItemName())
                .eqIfPresent(WmsInventoryDO::getSkuId, reqVO.getSkuId())
                .likeIfPresent(WmsItemSkuDO::getCode, reqVO.getSkuCode())
                .likeIfPresent(WmsItemSkuDO::getName, reqVO.getSkuName())
                .eqIfPresent(WmsInventoryDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(WmsInventoryDO::getAreaId, reqVO.getAreaId())
                .geIfPresent(WmsInventoryDO::getQuantity, reqVO.getMinQuantity());
        appendDimensionOrder(query, reqVO.getType());
        return selectJoinPage(reqVO, WmsInventoryDO.class, query);
    }

    default PageResult<WmsInventoryDO> selectPageGroupByWarehouse(WmsInventoryPageReqVO reqVO) {
        MPJLambdaWrapperX<WmsInventoryDO> query = new MPJLambdaWrapperX<WmsInventoryDO>()
                .selectMin(WmsInventoryDO::getId, WmsInventoryDO::getId)
                .selectAs(WmsInventoryDO::getWarehouseId, WmsInventoryDO::getWarehouseId)
                .selectAs("0", WmsInventoryDO::getAreaId)
                .selectAs(WmsInventoryDO::getSkuId, WmsInventoryDO::getSkuId)
                .selectSum(WmsInventoryDO::getQuantity, WmsInventoryDO::getQuantity)
                .innerJoin(WmsItemSkuDO.class, WmsItemSkuDO::getId, WmsInventoryDO::getSkuId)
                .innerJoin(WmsItemDO.class, WmsItemDO::getId, WmsItemSkuDO::getItemId)
                .likeIfPresent(WmsItemDO::getCode, reqVO.getItemCode())
                .likeIfPresent(WmsItemDO::getName, reqVO.getItemName())
                .eqIfPresent(WmsInventoryDO::getSkuId, reqVO.getSkuId())
                .likeIfPresent(WmsItemSkuDO::getCode, reqVO.getSkuCode())
                .likeIfPresent(WmsItemSkuDO::getName, reqVO.getSkuName())
                .eqIfPresent(WmsInventoryDO::getWarehouseId, reqVO.getWarehouseId());
        query.groupBy(WmsInventoryDO::getWarehouseId, WmsInventoryDO::getSkuId);
        if (reqVO.getMinQuantity() != null) {
            query.having("SUM(t.quantity) >= {0}", reqVO.getMinQuantity());
        }
        query.orderByAsc(WmsInventoryDO::getWarehouseId)
                .orderByAsc(WmsInventoryDO::getSkuId);
        return selectJoinPage(reqVO, WmsInventoryDO.class, query);
    }

    default Long selectCountBySkuId(Long skuId) {
        return selectCount(WmsInventoryDO::getSkuId, skuId);
    }

    private static void appendDimensionOrder(MPJLambdaWrapperX<WmsInventoryDO> query, String type) {
        if (StrUtil.equals(WmsInventoryPageReqVO.TYPE_WAREHOUSE, type)) {
            query.orderByAsc(WmsInventoryDO::getWarehouseId)
                    .orderByAsc(WmsItemSkuDO::getItemId)
                    .orderByAsc(WmsInventoryDO::getSkuId)
                    .orderByAsc(WmsInventoryDO::getId);
            return;
        }
        if (StrUtil.equals(WmsInventoryPageReqVO.TYPE_ITEM, type)) {
            query.orderByAsc(WmsItemSkuDO::getItemId)
                    .orderByAsc(WmsInventoryDO::getSkuId)
                    .orderByAsc(WmsInventoryDO::getWarehouseId)
                    .orderByAsc(WmsInventoryDO::getAreaId)
                    .orderByAsc(WmsInventoryDO::getId);
            return;
        }
        if (StrUtil.equals(WmsInventoryPageReqVO.TYPE_AREA, type)) {
            query.orderByAsc(WmsInventoryDO::getWarehouseId)
                    .orderByAsc(WmsInventoryDO::getAreaId)
                    .orderByAsc(WmsItemSkuDO::getItemId)
                    .orderByAsc(WmsInventoryDO::getSkuId)
                    .orderByAsc(WmsInventoryDO::getId);
            return;
        }
        throw new IllegalArgumentException("未知库存统计维度：" + type);
    }

}
