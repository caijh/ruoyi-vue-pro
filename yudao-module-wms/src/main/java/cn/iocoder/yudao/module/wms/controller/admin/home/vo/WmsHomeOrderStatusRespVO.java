package cn.iocoder.yudao.module.wms.controller.admin.home.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - WMS 首页单据状态统计 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WmsHomeOrderStatusRespVO {

    @Schema(description = "状态值", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer status;

    // TODO @AI：statusName 前端字段就翻译点？
    @Schema(description = "状态名", requiredMode = Schema.RequiredMode.REQUIRED, example = "草稿")
    private String statusName;

    @Schema(description = "单据数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private Long count;

}
