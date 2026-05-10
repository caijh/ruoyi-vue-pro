package cn.iocoder.yudao.module.wms.dal.mysql.md.item;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * WMS 商品 SKU Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface WmsItemSkuMapper extends BaseMapperX<WmsItemSkuDO> {

    default List<WmsItemSkuDO> selectListByItemId(Long itemId) {
        return selectList(new LambdaQueryWrapperX<WmsItemSkuDO>()
                .eq(WmsItemSkuDO::getItemId, itemId)
                .orderByAsc(WmsItemSkuDO::getId));
    }

    default List<WmsItemSkuDO> selectListByItemIds(Collection<Long> itemIds) {
        if (CollUtil.isEmpty(itemIds)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<WmsItemSkuDO>()
                .inIfPresent(WmsItemSkuDO::getItemId, itemIds)
                .orderByAsc(WmsItemSkuDO::getId));
    }

    default void deleteByItemId(Long itemId) {
        delete(WmsItemSkuDO::getItemId, itemId);
    }

}
