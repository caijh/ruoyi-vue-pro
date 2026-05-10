package cn.iocoder.yudao.module.wms.controller.admin.md.warehouse.vo.area;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - WMS 库区新增/修改 Request VO")
@Data
public class WmsWarehouseAreaSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "库区编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "A001")
    @NotEmpty(message = "库区编号不能为空")
    @Size(max = 20, message = "库区编号长度不能超过 20 个字符")
    private String code;

    @Schema(description = "库区名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "A 区")
    @NotEmpty(message = "库区名称不能为空")
    private String name;

    @Schema(description = "所属仓库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "所属仓库不能为空")
    private Long warehouseId;

    @Schema(description = "备注", example = "备注")
    private String remark;

}
