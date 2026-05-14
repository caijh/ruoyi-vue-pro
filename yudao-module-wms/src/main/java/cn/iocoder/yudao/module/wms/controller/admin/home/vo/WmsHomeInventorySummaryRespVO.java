package cn.iocoder.yudao.module.wms.controller.admin.home.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - WMS 首页库存汇总统计 Response VO")
@Data
public class WmsHomeInventorySummaryRespVO {

    // TODO @AI：一次性的 VO ，要不合并到 WmsHomeInventorySummaryRespVO 里？一个叫 ItemRank，一个叫 WarehouseRank？

    @Schema(description = "总库存数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000.00")
    private BigDecimal totalQuantity;

    @Schema(description = "商品库存占比列表")
    private List<WmsHomeInventoryItemRankRespVO> goodsShareList;

    @Schema(description = "仓库库存分布列表")
    private List<WmsHomeInventoryWarehouseRankRespVO> warehouseDistributionList;

}
