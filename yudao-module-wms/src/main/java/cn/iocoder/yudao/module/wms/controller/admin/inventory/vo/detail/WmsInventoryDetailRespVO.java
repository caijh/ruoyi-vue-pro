package cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - WMS 库存明细 Response VO")
@Data
public class WmsInventoryDetailRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long itemId;
    @Schema(description = "商品编码", example = "ITEM001")
    private String itemCode;
    @Schema(description = "商品名称", example = "红富士苹果")
    private String itemName;
    @Schema(description = "商品单位", example = "箱")
    private String unit;

    @Schema(description = "商品 SKU 编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "4096")
    private Long skuId;
    @Schema(description = "规格编号", example = "SKU001")
    private String skuCode;
    @Schema(description = "规格名称", example = "10kg 箱装")
    private String skuName;

    @Schema(description = "仓库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "8192")
    private Long warehouseId;
    @Schema(description = "仓库名称", example = "成品仓")
    private String warehouseName;
    @Schema(description = "库区编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Long areaId;
    @Schema(description = "库区名称", example = "A 区")
    private String areaName;

    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    private BigDecimal quantity;
    @Schema(description = "剩余数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "60.00")
    private BigDecimal remainQuantity;
    @Schema(description = "批号", example = "BATCH001")
    private String batchNo;
    @Schema(description = "生产日期")
    private LocalDateTime productionDate;
    @Schema(description = "过期日期")
    private LocalDateTime expirationDate;
    @Schema(description = "金额", example = "1000.00")
    private BigDecimal amount;
    @Schema(description = "备注", example = "备注")
    private String remark;

    @Schema(description = "操作单编号", example = "1024")
    private Long orderId;
    @Schema(description = "操作单号", example = "RK202605110001")
    private String orderNo;
    @Schema(description = "操作类型", example = "1")
    private Integer orderType;
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
