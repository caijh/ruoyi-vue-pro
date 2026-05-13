package cn.iocoder.yudao.module.wms.service.md.warehouse;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import cn.iocoder.yudao.module.wms.controller.admin.md.warehouse.vo.area.WmsWarehouseAreaPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.md.warehouse.vo.area.WmsWarehouseAreaSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.mysql.md.warehouse.WmsWarehouseAreaMapper;
import cn.iocoder.yudao.module.wms.framework.config.WmsProperties;
import cn.iocoder.yudao.module.wms.service.order.check.WmsCheckOrderDetailService;
import cn.iocoder.yudao.module.wms.service.order.check.WmsCheckOrderService;
import cn.iocoder.yudao.module.wms.service.order.movement.WmsMovementOrderDetailService;
import cn.iocoder.yudao.module.wms.service.order.movement.WmsMovementOrderService;
import cn.iocoder.yudao.module.wms.service.order.receipt.WmsReceiptOrderDetailService;
import cn.iocoder.yudao.module.wms.service.order.receipt.WmsReceiptOrderService;
import cn.iocoder.yudao.module.wms.service.order.shipment.WmsShipmentOrderDetailService;
import cn.iocoder.yudao.module.wms.service.order.shipment.WmsShipmentOrderService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.wms.enums.order.WmsOrderTypeConstants.*;

/**
 * WMS 库区 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsWarehouseAreaServiceImpl implements WmsWarehouseAreaService {

    @Resource
    private WmsWarehouseAreaMapper warehouseAreaMapper;
    @Resource
    private WmsWarehouseService warehouseService;
    @Resource
    private WmsProperties wmsProperties;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private WmsReceiptOrderService receiptOrderService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private WmsReceiptOrderDetailService receiptOrderDetailService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private WmsShipmentOrderService shipmentOrderService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private WmsShipmentOrderDetailService shipmentOrderDetailService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private WmsMovementOrderService movementOrderService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private WmsMovementOrderDetailService movementOrderDetailService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private WmsCheckOrderService checkOrderService;
    @Resource
    @Lazy // 延迟加载，避免循环依赖
    private WmsCheckOrderDetailService checkOrderDetailService;

    @Override
    public Long createWarehouseArea(WmsWarehouseAreaSaveReqVO createReqVO) {
        validateAreaEnabled();
        // 校验数据
        validateWarehouseAreaSaveData(createReqVO);

        // 插入
        WmsWarehouseAreaDO area = BeanUtils.toBean(createReqVO, WmsWarehouseAreaDO.class);
        warehouseAreaMapper.insert(area);
        // 返回
        return area.getId();
    }

    @Override
    public void updateWarehouseArea(WmsWarehouseAreaSaveReqVO updateReqVO) {
        validateAreaEnabled();
        // 校验存在
        validateWarehouseAreaExists(updateReqVO.getId());
        // 校验数据
        validateWarehouseAreaSaveData(updateReqVO);

        // 更新
        WmsWarehouseAreaDO updateObj = BeanUtils.toBean(updateReqVO, WmsWarehouseAreaDO.class);
        warehouseAreaMapper.updateById(updateObj);
    }

    private void validateWarehouseAreaSaveData(WmsWarehouseAreaSaveReqVO reqVO) {
        warehouseService.validateWarehouseExists(reqVO.getWarehouseId());
        validateWarehouseAreaNameUnique(reqVO.getId(), reqVO.getWarehouseId(), reqVO.getName());
        validateWarehouseAreaCodeUnique(reqVO.getId(), reqVO.getCode());
    }

    private void validateWarehouseAreaNameUnique(Long id, Long warehouseId, String name) {
        WmsWarehouseAreaDO area = warehouseAreaMapper.selectByName(warehouseId, name);
        if (area == null) {
            return;
        }
        if (ObjectUtil.notEqual(area.getId(), id)) {
            throw exception(WAREHOUSE_AREA_NAME_DUPLICATE);
        }
    }

    private void validateWarehouseAreaCodeUnique(Long id, String code) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        WmsWarehouseAreaDO area = warehouseAreaMapper.selectByCode(code);
        if (area == null) {
            return;
        }
        if (ObjectUtil.notEqual(area.getId(), id)) {
            throw exception(WAREHOUSE_AREA_CODE_DUPLICATE);
        }
    }

    @Override
    public void deleteWarehouseArea(Long id) {
        validateAreaEnabled();
        // 校验存在
        validateWarehouseAreaExists(id);
        // 校验未被单据使用
        validateWarehouseAreaUnused(id);

        // 删除
        warehouseAreaMapper.deleteById(id);
    }

    private void validateWarehouseAreaUnused(Long id) {
        if (receiptOrderService.getReceiptOrderCountByAreaId(id) > 0
                || receiptOrderDetailService.getReceiptOrderDetailCountByAreaId(id) > 0) {
            throw exception(WAREHOUSE_AREA_HAS_ORDER, ORDER_NAME_RECEIPT);
        }
        if (shipmentOrderService.getShipmentOrderCountByAreaId(id) > 0
                || shipmentOrderDetailService.getShipmentOrderDetailCountByAreaId(id) > 0) {
            throw exception(WAREHOUSE_AREA_HAS_ORDER, ORDER_NAME_SHIPMENT);
        }
        if (movementOrderService.getMovementOrderCountByAreaId(id) > 0
                || movementOrderDetailService.getMovementOrderDetailCountByAreaId(id) > 0) {
            throw exception(WAREHOUSE_AREA_HAS_ORDER, ORDER_NAME_MOVEMENT);
        }
        if (checkOrderService.getCheckOrderCountByAreaId(id) > 0
                || checkOrderDetailService.getCheckOrderDetailCountByAreaId(id) > 0) {
            throw exception(WAREHOUSE_AREA_HAS_ORDER, ORDER_NAME_CHECK);
        }
    }

    @Override
    public WmsWarehouseAreaDO validateWarehouseAreaExists(Long id) {
        validateAreaEnabled();
        WmsWarehouseAreaDO area = warehouseAreaMapper.selectById(id);
        if (area == null) {
            throw exception(WAREHOUSE_AREA_NOT_EXISTS);
        }
        return area;
    }

    @Override
    public Long validateAndNormalizeWarehouseAreaId(Long id, Long warehouseId) {
        if (!wmsProperties.isAreaEnabled()) {
            return WmsWarehouseAreaDO.ID_EMPTY;
        }
        if (ObjectUtils.equalsAny(id, null, WmsWarehouseAreaDO.ID_EMPTY)) {
            return WmsWarehouseAreaDO.ID_EMPTY;
        }
        WmsWarehouseAreaDO area = validateWarehouseAreaExists(id);
        if (ObjectUtil.notEqual(area.getWarehouseId(), warehouseId)) {
            throw exception(WAREHOUSE_AREA_NOT_MATCH_WAREHOUSE);
        }
        return id;
    }

    @Override
    public WmsWarehouseAreaDO getWarehouseArea(Long id) {
        validateAreaEnabled();
        return warehouseAreaMapper.selectById(id);
    }

    @Override
    public PageResult<WmsWarehouseAreaDO> getWarehouseAreaPage(WmsWarehouseAreaPageReqVO pageReqVO) {
        validateAreaEnabled();
        return warehouseAreaMapper.selectPage(pageReqVO);
    }

    @Override
    public List<WmsWarehouseAreaDO> getWarehouseAreaList(Long warehouseId) {
        validateAreaEnabled();
        return warehouseAreaMapper.selectListByWarehouseId(warehouseId);
    }

    @Override
    public List<WmsWarehouseAreaDO> getWarehouseAreaList(Collection<Long> ids) {
        validateAreaEnabled();
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return warehouseAreaMapper.selectByIds(ids);
    }

    private void validateAreaEnabled() {
        if (!wmsProperties.isAreaEnabled()) {
            throw exception(WMS_AREA_DISABLED);
        }
    }

}
