package cn.iocoder.yudao.module.wms.controller.admin.warehouse;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.wms.controller.admin.warehouse.vo.area.WmsWarehouseAreaPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.warehouse.vo.area.WmsWarehouseAreaRespVO;
import cn.iocoder.yudao.module.wms.controller.admin.warehouse.vo.area.WmsWarehouseAreaSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.warehouse.WmsWarehouseDO;
import cn.iocoder.yudao.module.wms.service.warehouse.WmsWarehouseAreaService;
import cn.iocoder.yudao.module.wms.service.warehouse.WmsWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.getFirst;

@Tag(name = "管理后台 - WMS 库区")
@RestController
@RequestMapping("/wms/warehouse-area")
@Validated
public class WmsWarehouseAreaController {

    @Resource
    private WmsWarehouseAreaService warehouseAreaService;
    @Resource
    private WmsWarehouseService warehouseService;

    @PostMapping("/create")
    @Operation(summary = "创建库区")
    @PreAuthorize("@ss.hasPermission('wms:warehouse-area:create')")
    public CommonResult<Long> createWarehouseArea(@Valid @RequestBody WmsWarehouseAreaSaveReqVO createReqVO) {
        return success(warehouseAreaService.createWarehouseArea(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新库区")
    @PreAuthorize("@ss.hasPermission('wms:warehouse-area:update')")
    public CommonResult<Boolean> updateWarehouseArea(@Valid @RequestBody WmsWarehouseAreaSaveReqVO updateReqVO) {
        warehouseAreaService.updateWarehouseArea(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除库区")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('wms:warehouse-area:delete')")
    public CommonResult<Boolean> deleteWarehouseArea(@RequestParam("id") Long id) {
        warehouseAreaService.deleteWarehouseArea(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得库区")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('wms:warehouse-area:query')")
    public CommonResult<WmsWarehouseAreaRespVO> getWarehouseArea(@RequestParam("id") Long id) {
        WmsWarehouseAreaDO area = warehouseAreaService.getWarehouseArea(id);
        return success(buildWarehouseAreaRespVO(area));
    }

    @GetMapping("/page")
    @Operation(summary = "获得库区分页")
    @PreAuthorize("@ss.hasPermission('wms:warehouse-area:query')")
    public CommonResult<PageResult<WmsWarehouseAreaRespVO>> getWarehouseAreaPage(@Valid WmsWarehouseAreaPageReqVO pageReqVO) {
        PageResult<WmsWarehouseAreaDO> pageResult = warehouseAreaService.getWarehouseAreaPage(pageReqVO);
        return success(new PageResult<>(buildWarehouseAreaRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得库区精简列表", description = "可按仓库过滤")
    @PreAuthorize("@ss.hasPermission('wms:warehouse-area:query')")
    public CommonResult<List<WmsWarehouseAreaRespVO>> getWarehouseAreaSimpleList(
            @Parameter(name = "warehouseId", description = "仓库编号") @RequestParam(value = "warehouseId", required = false) Long warehouseId) {
        List<WmsWarehouseAreaDO> list = warehouseAreaService.getWarehouseAreaList(warehouseId);
        return success(buildWarehouseAreaRespVOList(list));
    }

    // ==================== 拼接 VO ====================

    private WmsWarehouseAreaRespVO buildWarehouseAreaRespVO(WmsWarehouseAreaDO area) {
        if (area == null) {
            return null;
        }
        return getFirst(buildWarehouseAreaRespVOList(Collections.singletonList(area)));
    }

    private List<WmsWarehouseAreaRespVO> buildWarehouseAreaRespVOList(List<WmsWarehouseAreaDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<Long, WmsWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(convertSet(list, WmsWarehouseAreaDO::getWarehouseId));
        return BeanUtils.toBean(list, WmsWarehouseAreaRespVO.class, vo ->
                MapUtils.findAndThen(warehouseMap, vo.getWarehouseId(), warehouse -> vo.setWarehouseName(warehouse.getName())));
    }

}
