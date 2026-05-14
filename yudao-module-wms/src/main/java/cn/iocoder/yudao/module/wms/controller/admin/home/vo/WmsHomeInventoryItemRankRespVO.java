package cn.iocoder.yudao.module.wms.controller.admin.home.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - WMS 首页商品库存排行 Response VO")
@Data
public class WmsHomeInventoryItemRankRespVO {

    // TODO @AI：改成 id、name 会不会更好？

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long itemId;

    @Schema(description = "商品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "A4 复印纸")
    private String itemName;

    @Schema(description = "库存数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    private BigDecimal quantity;

}
