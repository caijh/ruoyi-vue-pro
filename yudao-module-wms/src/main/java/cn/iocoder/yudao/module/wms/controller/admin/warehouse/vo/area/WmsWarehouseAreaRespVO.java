package cn.iocoder.yudao.module.wms.controller.admin.warehouse.vo.area;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - WMS 库区 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WmsWarehouseAreaRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "库区编号", example = "A001")
    @ExcelProperty("库区编号")
    private String code;

    @Schema(description = "库区名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "A 区")
    @ExcelProperty("库区名称")
    private String name;

    @Schema(description = "所属仓库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("所属仓库编号")
    private Long warehouseId;

    @Schema(description = "所属仓库名称", example = "原料仓")
    @ExcelProperty("所属仓库名称")
    private String warehouseName;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
