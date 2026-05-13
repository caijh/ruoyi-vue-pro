package cn.iocoder.yudao.module.wms.service.order.movement;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.wms.controller.admin.order.movement.vo.order.WmsMovementOrderPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.order.movement.vo.order.WmsMovementOrderSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.movement.WmsMovementOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.movement.WmsMovementOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.movement.WmsMovementOrderMapper;
import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsOrderStatusEnum;
import cn.iocoder.yudao.module.wms.service.inventory.WmsInventoryService;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseAreaService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * WMS 移库单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsMovementOrderServiceImpl implements WmsMovementOrderService {

    @Resource
    private WmsMovementOrderMapper movementOrderMapper;
    @Resource
    private WmsMovementOrderDetailService movementOrderDetailService;
    @Resource
    private WmsWarehouseService warehouseService;
    @Resource
    private WmsWarehouseAreaService warehouseAreaService;
    @Resource
    private WmsInventoryService inventoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMovementOrder(WmsMovementOrderSaveReqVO createReqVO) {
        // 1. 校验移库单保存数据
        validateMovementOrderSaveData(createReqVO);

        // 2.1 插入移库单
        WmsMovementOrderDO order = BeanUtils.toBean(createReqVO, WmsMovementOrderDO.class);
        order.setStatus(WmsOrderStatusEnum.PREPARE.getStatus());
        movementOrderMapper.insert(order);
        // 2.2 插入移库单明细
        movementOrderDetailService.createMovementOrderDetailList(order.getId(), createReqVO);
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMovementOrder(WmsMovementOrderSaveReqVO updateReqVO) {
        // 1. 校验移库单保存数据
        validateMovementOrderPrepare(updateReqVO.getId());
        validateMovementOrderSaveData(updateReqVO);

        // 2.1 更新移库单
        WmsMovementOrderDO updateObj = BeanUtils.toBean(updateReqVO, WmsMovementOrderDO.class)
                .setStatus(WmsOrderStatusEnum.PREPARE.getStatus());
        movementOrderMapper.updateById(updateObj);
        // 2.2 更新移库单明细
        movementOrderDetailService.updateMovementOrderDetailList(updateReqVO.getId(), updateReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMovementOrder(Long id) {
        // 1. 校验存在，且可删除
        WmsMovementOrderDO order = validateMovementOrderExists(id);
        if (ObjectUtil.notEqual(order.getStatus(), WmsOrderStatusEnum.PREPARE.getStatus())
                && ObjectUtil.notEqual(order.getStatus(), WmsOrderStatusEnum.CANCELED.getStatus())) {
            throw exception(MOVEMENT_ORDER_STATUS_NOT_DELETABLE);
        }

        // 2.1 删除移库单
        movementOrderMapper.deleteById(id);
        // 2.2 删除移库单明细
        movementOrderDetailService.deleteMovementOrderDetailListByOrderId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeMovementOrder(Long id) {
        // 1.1 校验存在，且草稿
        WmsMovementOrderDO order = validateMovementOrderPrepare(id);
        // 1.2 校验移库单明细存在
        List<WmsMovementOrderDetailDO> details = movementOrderDetailService.validateMovementOrderDetailListExists(id);

        // 2. 完成移库单
        movementOrderMapper.updateById(new WmsMovementOrderDO().setId(id)
                .setStatus(WmsOrderStatusEnum.FINISHED.getStatus()));

        // 3. 移动库存
        changeInventory(order, details);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMovementOrder(Long id) {
        // 1. 校验存在，且草稿
        validateMovementOrderPrepare(id);

        // 2. 作废移库单
        movementOrderMapper.updateById(new WmsMovementOrderDO().setId(id)
                .setStatus(WmsOrderStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public WmsMovementOrderDO getMovementOrder(Long id) {
        return movementOrderMapper.selectById(id);
    }

    @Override
    public PageResult<WmsMovementOrderDO> getMovementOrderPage(WmsMovementOrderPageReqVO pageReqVO) {
        return movementOrderMapper.selectPage(pageReqVO);
    }

    @Override
    public long getMovementOrderCountByWarehouseId(Long warehouseId) {
        return movementOrderMapper.selectCountByWarehouseId(warehouseId);
    }

    @Override
    public long getMovementOrderCountByAreaId(Long areaId) {
        return movementOrderMapper.selectCountByAreaId(areaId);
    }

    private void validateMovementOrderSaveData(WmsMovementOrderSaveReqVO reqVO) {
        // 校验移库单号唯一
        validateMovementOrderNoUnique(reqVO.getId(), reqVO.getNo());
        // 校验仓库、库区存在
        warehouseService.validateWarehouseExists(reqVO.getSourceWarehouseId());
        warehouseService.validateWarehouseExists(reqVO.getTargetWarehouseId());
        reqVO.setSourceAreaId(warehouseAreaService.validateAndNormalizeWarehouseAreaId(reqVO.getSourceAreaId(),
                reqVO.getSourceWarehouseId()));
        reqVO.setTargetAreaId(warehouseAreaService.validateAndNormalizeWarehouseAreaId(reqVO.getTargetAreaId(),
                reqVO.getTargetWarehouseId()));
        // 校验来源和目标不能相同
        if (ObjectUtil.equal(reqVO.getSourceWarehouseId(), reqVO.getTargetWarehouseId())
                && ObjectUtil.equal(reqVO.getSourceAreaId(), reqVO.getTargetAreaId())) {
            throw exception(MOVEMENT_ORDER_WAREHOUSE_AREA_SAME);
        }
    }

    private void validateMovementOrderNoUnique(Long id, String no) {
        WmsMovementOrderDO order = movementOrderMapper.selectByNo(no);
        if (order == null) {
            return;
        }
        if (id == null || ObjectUtil.notEqual(order.getId(), id)) {
            throw exception(MOVEMENT_ORDER_NO_DUPLICATE);
        }
    }

    private WmsMovementOrderDO validateMovementOrderExists(Long id) {
        WmsMovementOrderDO order = id == null ? null : movementOrderMapper.selectById(id);
        if (order == null) {
            throw exception(MOVEMENT_ORDER_NOT_EXISTS);
        }
        return order;
    }

    /**
     * 校验移库单存在且为草稿状态
     *
     * @param id 移库单编号
     * @return 移库单
     */
    private WmsMovementOrderDO validateMovementOrderPrepare(Long id) {
        WmsMovementOrderDO order = validateMovementOrderExists(id);
        if (ObjectUtil.notEqual(order.getStatus(), WmsOrderStatusEnum.PREPARE.getStatus())) {
            throw exception(MOVEMENT_ORDER_STATUS_NOT_PREPARE);
        }
        return order;
    }

    /**
     * 移动移库单对应库存。
     *
     * 移库会生成两条库存变更：来源仓库扣减库存，目标仓库增加库存；批次模式下，来源库存明细用
     * inventoryDetailId 扣减剩余数量，目标仓库生成新的库存明细。
     *
     * @param order 移库单
     * @param details 移库单明细列表
     */
    private void changeInventory(WmsMovementOrderDO order, List<WmsMovementOrderDetailDO> details) {
        List<WmsInventoryChangeReqDTO.Item> items = new ArrayList<>(details.size() * 2);
        for (WmsMovementOrderDetailDO detail : details) {
            items.add(BeanUtils.toBean(detail, WmsInventoryChangeReqDTO.Item.class)
                    .setWarehouseId(detail.getSourceWarehouseId()).setAreaId(detail.getSourceAreaId())
                    .setQuantity(detail.getQuantity().negate()));
            items.add(BeanUtils.toBean(detail, WmsInventoryChangeReqDTO.Item.class)
                    .setWarehouseId(detail.getTargetWarehouseId()).setAreaId(detail.getTargetAreaId())
                    .setInventoryDetailId(null).setQuantity(detail.getQuantity()));
        }
        inventoryService.changeInventory(new WmsInventoryChangeReqDTO()
                .setOrderId(order.getId()).setOrderNo(order.getNo())
                .setOrderType(WmsInventoryOrderTypeEnum.MOVEMENT.getType()).setItems(items));
    }

}
