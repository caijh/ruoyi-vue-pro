package cn.iocoder.yudao.module.wms.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * WMS 错误码枚举类
 * <p>
 * wms 系统，使用 1-060-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== WMS 配置 1-060-000-000 ==========
    ErrorCode WMS_AREA_DISABLED = new ErrorCode(1_060_000_000, "WMS 库区模式未开启");

    // ========== WMS 基础数据-仓库 1-060-100-000 ==========
    ErrorCode WAREHOUSE_NOT_EXISTS = new ErrorCode(1_060_100_000, "仓库不存在");
    ErrorCode WAREHOUSE_NAME_DUPLICATE = new ErrorCode(1_060_100_001, "仓库名称重复");
    ErrorCode WAREHOUSE_CODE_DUPLICATE = new ErrorCode(1_060_100_002, "仓库编号重复");
    ErrorCode WAREHOUSE_HAS_AREA = new ErrorCode(1_060_100_003, "删除失败！请先删除该仓库下的库区！");

    // ========== WMS 基础数据-库区 1-060-101-000 ==========
    ErrorCode WAREHOUSE_AREA_NOT_EXISTS = new ErrorCode(1_060_101_000, "库区不存在");
    ErrorCode WAREHOUSE_AREA_NAME_DUPLICATE = new ErrorCode(1_060_101_001, "库区名称重复");
    ErrorCode WAREHOUSE_AREA_CODE_DUPLICATE = new ErrorCode(1_060_101_002, "库区编号重复");

    // ========== WMS 基础数据-商品分类 1-060-102-000 ==========
    ErrorCode ITEM_CATEGORY_NOT_EXISTS = new ErrorCode(1_060_102_000, "商品分类不存在");
    ErrorCode ITEM_CATEGORY_NAME_DUPLICATE = new ErrorCode(1_060_102_001, "商品分类名称重复");
    ErrorCode ITEM_CATEGORY_PARENT_NOT_EXISTS = new ErrorCode(1_060_102_002, "父商品分类不存在");
    ErrorCode ITEM_CATEGORY_PARENT_ERROR = new ErrorCode(1_060_102_003, "不能设置自己为父商品分类");
    ErrorCode ITEM_CATEGORY_PARENT_IS_CHILD = new ErrorCode(1_060_102_004, "不能设置自己的子商品分类为父商品分类");
    ErrorCode ITEM_CATEGORY_HAS_CHILDREN = new ErrorCode(1_060_102_005, "删除失败！请先删除该分类下的子分类！");

    // ========== WMS 基础数据-商品品牌 1-060-103-000 ==========
    ErrorCode ITEM_BRAND_NOT_EXISTS = new ErrorCode(1_060_103_000, "商品品牌不存在");

}
