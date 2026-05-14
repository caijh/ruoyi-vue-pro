package cn.iocoder.yudao.module.wms.controller.admin.home.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - WMS 首页单据汇总统计 Response VO")
@Data
public class WmsHomeOrderSummaryRespVO {

    // TODO @AI：orderType 缩写成 type？

    @Schema(description = "单据类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer orderType;

    // TODO @AI：type 改成 dict tag 去翻译？或者字典工具类？
    @Schema(description = "单据类型名", requiredMode = Schema.RequiredMode.REQUIRED, example = "入库单")
    private String orderTypeName;

    @Schema(description = "单据总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private Long total;

    // TODO @AI：statuses；状态分布；
    @Schema(description = "状态分布")
    private List<WmsHomeOrderStatusRespVO> statusList;

}
