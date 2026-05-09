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

}
