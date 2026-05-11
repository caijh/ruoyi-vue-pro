package cn.iocoder.yudao.module.wms.dal.mysql.inventory;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.detail.WmsInventoryDetailPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDetailDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * WMS 库存明细 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface WmsInventoryDetailMapper extends BaseMapperX<WmsInventoryDetailDO> {

    default PageResult<WmsInventoryDetailDO> selectPage(WmsInventoryDetailPageReqVO reqVO) {
        MPJLambdaWrapperX<WmsInventoryDetailDO> query = new MPJLambdaWrapperX<WmsInventoryDetailDO>()
                .selectAll(WmsInventoryDetailDO.class)
                .innerJoin(WmsItemSkuDO.class, WmsItemSkuDO::getId, WmsInventoryDetailDO::getSkuId)
                .innerJoin(WmsItemDO.class, WmsItemDO::getId, WmsItemSkuDO::getItemId)
                .likeIfPresent(WmsItemDO::getCode, reqVO.getItemCode())
                .likeIfPresent(WmsItemDO::getName, reqVO.getItemName())
                .eqIfPresent(WmsInventoryDetailDO::getSkuId, reqVO.getSkuId())
                .likeIfPresent(WmsItemSkuDO::getCode, reqVO.getSkuCode())
                .likeIfPresent(WmsItemSkuDO::getName, reqVO.getSkuName())
                .eqIfPresent(WmsInventoryDetailDO::getWarehouseId, reqVO.getWarehouseId())
                .eqIfPresent(WmsInventoryDetailDO::getAreaId, reqVO.getAreaId())
                .likeIfPresent(WmsInventoryDetailDO::getBatchNo, reqVO.getBatchNo())
                .betweenIfPresent(WmsInventoryDetailDO::getCreateTime, reqVO.getCreateTime());
        appendDaysToExpiresQuery(query, reqVO.getDaysToExpires());
        appendDimensionOrder(query, reqVO.getType());
        return selectJoinPage(reqVO, WmsInventoryDetailDO.class, query);
    }

    private static void appendDaysToExpiresQuery(MPJLambdaWrapperX<WmsInventoryDetailDO> query, Integer daysToExpires) {
        if (daysToExpires == null) {
            return;
        }
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        query.geIfPresent(WmsInventoryDetailDO::getExpirationDate, startTime)
                .leIfPresent(WmsInventoryDetailDO::getExpirationDate, startTime.plusDays(daysToExpires));
    }

    private static void appendDimensionOrder(MPJLambdaWrapperX<WmsInventoryDetailDO> query, String type) {
        if (StrUtil.equals(WmsInventoryDetailPageReqVO.TYPE_WAREHOUSE, type)) {
            query.orderByAsc(WmsInventoryDetailDO::getWarehouseId)
                    .orderByAsc(WmsInventoryDetailDO::getAreaId)
                    .orderByAsc(WmsItemSkuDO::getItemId)
                    .orderByAsc(WmsInventoryDetailDO::getSkuId)
                    .orderByAsc(WmsInventoryDetailDO::getId);
            return;
        }
        if (StrUtil.equals(WmsInventoryDetailPageReqVO.TYPE_ITEM, type)) {
            query.orderByAsc(WmsItemSkuDO::getItemId)
                    .orderByAsc(WmsInventoryDetailDO::getSkuId)
                    .orderByAsc(WmsInventoryDetailDO::getWarehouseId)
                    .orderByAsc(WmsInventoryDetailDO::getAreaId)
                    .orderByAsc(WmsInventoryDetailDO::getId);
            return;
        }
        throw new IllegalArgumentException("未知库存明细统计维度：" + type);
    }

}
