package cn.iocoder.yudao.module.wms.controller.admin.warehouse.vo.area;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - WMS 库区分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WmsWarehouseAreaPageReqVO extends PageParam {

    @Schema(description = "库区编号", example = "A001")
    private String code;

    @Schema(description = "库区名称", example = "A 区")
    private String name;

    @Schema(description = "所属仓库编号", example = "1024")
    private Long warehouseId;

}
