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
    ErrorCode WAREHOUSE_AREA_NOT_MATCH_WAREHOUSE = new ErrorCode(1_060_101_003, "库区不属于仓库");

    // ========== WMS 基础数据-商品分类 1-060-102-000 ==========
    ErrorCode ITEM_CATEGORY_NOT_EXISTS = new ErrorCode(1_060_102_000, "商品分类不存在");
    ErrorCode ITEM_CATEGORY_NAME_DUPLICATE = new ErrorCode(1_060_102_001, "商品分类名称重复");
    ErrorCode ITEM_CATEGORY_PARENT_NOT_EXISTS = new ErrorCode(1_060_102_002, "父商品分类不存在");
    ErrorCode ITEM_CATEGORY_PARENT_ERROR = new ErrorCode(1_060_102_003, "不能设置自己为父商品分类");
    ErrorCode ITEM_CATEGORY_PARENT_IS_CHILD = new ErrorCode(1_060_102_004, "不能设置自己的子商品分类为父商品分类");
    ErrorCode ITEM_CATEGORY_HAS_CHILDREN = new ErrorCode(1_060_102_005, "删除失败！请先删除该分类下的子分类！");
    ErrorCode ITEM_CATEGORY_HAS_ITEM = new ErrorCode(1_060_102_006, "删除失败！分类已被商品使用！");

    // ========== WMS 基础数据-商品品牌 1-060-103-000 ==========
    ErrorCode ITEM_BRAND_NOT_EXISTS = new ErrorCode(1_060_103_000, "商品品牌不存在");
    ErrorCode ITEM_BRAND_HAS_ITEM = new ErrorCode(1_060_103_001, "删除失败！品牌已被商品使用！");

    // ========== WMS 基础数据-商品 1-060-104-000 ==========
    ErrorCode ITEM_NOT_EXISTS = new ErrorCode(1_060_104_000, "商品不存在");
    ErrorCode ITEM_NAME_DUPLICATE = new ErrorCode(1_060_104_001, "商品名称重复");
    ErrorCode ITEM_SKU_REQUIRED = new ErrorCode(1_060_104_002, "至少包含一个商品规格");
    ErrorCode ITEM_SKU_NAME_DUPLICATE = new ErrorCode(1_060_104_003, "商品规格名称【{}】重复");
    ErrorCode ITEM_SKU_NOT_EXISTS = new ErrorCode(1_060_104_004, "商品规格不存在");
    ErrorCode ITEM_SKU_HAS_INVENTORY = new ErrorCode(1_060_104_005, "删除失败！商品规格【{}】已被库存业务使用！");

    // ========== WMS 基础数据-往来企业 1-060-105-000 ==========
    ErrorCode MERCHANT_NOT_EXISTS = new ErrorCode(1_060_105_000, "往来企业不存在");
    ErrorCode MERCHANT_NOT_SUPPLIER = new ErrorCode(1_060_105_001, "往来企业必须是供应商或客户/供应商类型");

    // ========== WMS 入库单 1-060-200-000 ==========
    ErrorCode RECEIPT_ORDER_NOT_EXISTS = new ErrorCode(1_060_200_000, "入库单不存在");
    ErrorCode RECEIPT_ORDER_NO_DUPLICATE = new ErrorCode(1_060_200_001, "入库单号重复");
    ErrorCode RECEIPT_ORDER_STATUS_NOT_PREPARE = new ErrorCode(1_060_200_002, "入库单状态不是草稿，不能操作");
    ErrorCode RECEIPT_ORDER_DETAIL_REQUIRED = new ErrorCode(1_060_200_003, "入库单至少包含一条明细");
    ErrorCode RECEIPT_ORDER_AREA_REQUIRED = new ErrorCode(1_060_200_004, "库区模式下，入库单库区不能为空");
    ErrorCode RECEIPT_ORDER_DETAIL_NOT_EXISTS = new ErrorCode(1_060_200_007, "入库单明细不存在");

}
