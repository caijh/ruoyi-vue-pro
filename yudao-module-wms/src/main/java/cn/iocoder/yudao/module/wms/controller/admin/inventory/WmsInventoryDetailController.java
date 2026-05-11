package cn.iocoder.yudao.module.wms.controller.admin.inventory;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.detail.WmsInventoryDetailPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.detail.WmsInventoryDetailRespVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDetailDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseDO;
import cn.iocoder.yudao.module.wms.framework.config.WmsProperties;
import cn.iocoder.yudao.module.wms.service.inventory.WmsInventoryDetailService;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemService;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemSkuService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseAreaService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - WMS 库存明细")
@RestController
@RequestMapping("/wms/inventory-detail")
@Validated
public class WmsInventoryDetailController {

    @Resource
    private WmsInventoryDetailService inventoryDetailService;
    @Resource
    private WmsItemService itemService;
    @Resource
    private WmsItemSkuService itemSkuService;
    @Resource
    private WmsWarehouseService warehouseService;
    @Resource
    private WmsWarehouseAreaService warehouseAreaService;
    @Resource
    private WmsProperties wmsProperties;

    @GetMapping("/page")
    @Operation(summary = "获得库存明细分页")
    @PreAuthorize("@ss.hasPermission('wms:inventory-detail:query')")
    public CommonResult<PageResult<WmsInventoryDetailRespVO>> getInventoryDetailPage(
            @Valid WmsInventoryDetailPageReqVO pageReqVO) {
        PageResult<WmsInventoryDetailDO> pageResult = inventoryDetailService.getInventoryDetailPage(pageReqVO);
        return success(new PageResult<>(buildInventoryDetailRespVOList(pageResult.getList()), pageResult.getTotal()));
    }

    // ==================== 拼接 VO ====================

    private List<WmsInventoryDetailRespVO> buildInventoryDetailRespVOList(List<WmsInventoryDetailDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<Long, WmsItemSkuDO> skuMap = itemSkuService.getItemSkuMap(convertSet(list, WmsInventoryDetailDO::getSkuId));
        Map<Long, WmsItemDO> itemMap = itemService.getItemMap(convertSet(skuMap.values(), WmsItemSkuDO::getItemId));
        Map<Long, WmsWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                convertSet(list, WmsInventoryDetailDO::getWarehouseId));
        Map<Long, WmsWarehouseAreaDO> areaMap = wmsProperties.isAreaEnabled()
                ? warehouseAreaService.getWarehouseAreaMap(convertSet(list, WmsInventoryDetailDO::getAreaId))
                : Collections.emptyMap();
        return BeanUtils.toBean(list, WmsInventoryDetailRespVO.class, vo -> {
            MapUtils.findAndThen(skuMap, vo.getSkuId(), sku -> {
                vo.setSkuCode(sku.getCode()).setSkuName(sku.getName()).setItemId(sku.getItemId());
                MapUtils.findAndThen(itemMap, sku.getItemId(), item -> vo.setItemCode(item.getCode())
                        .setItemName(item.getName()).setUnit(item.getUnit()));
            });
            MapUtils.findAndThen(warehouseMap, vo.getWarehouseId(), warehouse -> vo.setWarehouseName(warehouse.getName()));
            MapUtils.findAndThen(areaMap, vo.getAreaId(), area -> vo.setAreaName(area.getName()));
        });
    }

}
