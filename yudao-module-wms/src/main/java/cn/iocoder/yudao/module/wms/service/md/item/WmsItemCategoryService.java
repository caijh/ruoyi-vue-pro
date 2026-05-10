package cn.iocoder.yudao.module.wms.service.md.item;

import cn.iocoder.yudao.module.wms.controller.admin.md.item.vo.category.WmsItemCategoryListReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.md.item.vo.category.WmsItemCategorySaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemCategoryDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * WMS 商品分类 Service 接口
 *
 * @author 芋道源码
 */
public interface WmsItemCategoryService {

    /**
     * 创建商品分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createItemCategory(@Valid WmsItemCategorySaveReqVO createReqVO);

    /**
     * 更新商品分类
     *
     * @param updateReqVO 更新信息
     */
    void updateItemCategory(@Valid WmsItemCategorySaveReqVO updateReqVO);

    /**
     * 删除商品分类
     *
     * @param id 编号
     */
    void deleteItemCategory(Long id);

    /**
     * 校验商品分类存在
     *
     * @param id 编号
     * @return 商品分类
     */
    WmsItemCategoryDO validateItemCategoryExists(Long id);

    /**
     * 获得商品分类
     *
     * @param id 编号
     * @return 商品分类
     */
    WmsItemCategoryDO getItemCategory(Long id);

    /**
     * 获得商品分类列表
     *
     * @param listReqVO 查询条件
     * @return 商品分类列表
     */
    List<WmsItemCategoryDO> getItemCategoryList(WmsItemCategoryListReqVO listReqVO);

}
