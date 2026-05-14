package cn.iocoder.yudao.module.wms.controller.admin.home.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - WMS 首页单据趋势 Response VO")
@Data
public class WmsHomeOrderTrendRespVO {

    // TODO @AI：可以使用 localdatetime？前端按需翻译？无非是 time 是 00 00 00 这样。灵活性更大。
    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-05-14")
    private String date;

    @Schema(description = "入库单数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private Long receiptCount;

    @Schema(description = "出库单数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private Long shipmentCount;

    @Schema(description = "移库单数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Long movementCount;

    @Schema(description = "盘库单数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long checkCount;

}
