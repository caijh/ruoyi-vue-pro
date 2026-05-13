package cn.iocoder.yudao.module.wms.service.order.movement;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.wms.controller.admin.order.movement.vo.order.WmsMovementOrderSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.movement.WmsMovementOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.movement.WmsMovementOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.movement.WmsMovementOrderDetailMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.order.movement.WmsMovementOrderMapper;
import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsOrderStatusEnum;
import cn.iocoder.yudao.module.wms.service.inventory.WmsInventoryService;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemSkuService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.MOVEMENT_ORDER_DETAIL_REQUIRED;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.MOVEMENT_ORDER_STATUS_NOT_DELETABLE;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.MOVEMENT_ORDER_WAREHOUSE_SAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Import({WmsMovementOrderServiceImpl.class, WmsMovementOrderDetailServiceImpl.class})
public class WmsMovementOrderServiceImplTest extends BaseDbUnitTest {

    @Resource
    private WmsMovementOrderServiceImpl movementOrderService;

    @Resource
    private WmsMovementOrderMapper movementOrderMapper;
    @Resource
    private WmsMovementOrderDetailMapper movementOrderDetailMapper;

    @MockitoBean
    private WmsWarehouseService warehouseService;
    @MockitoBean
    private WmsItemSkuService itemSkuService;
    @MockitoBean
    private WmsInventoryService inventoryService;

    @Test
    public void testCompleteMovementOrder_success() {
        // mock 数据
        Long sourceWarehouseId = 100L;
        Long targetWarehouseId = 200L;
        Long skuId = 300L;
        WmsMovementOrderDO order = createMovementOrder(sourceWarehouseId, targetWarehouseId);
        movementOrderMapper.insert(order);
        movementOrderDetailMapper.insert(createMovementOrderDetail(order.getId(), skuId,
                sourceWarehouseId, targetWarehouseId));

        // 调用
        movementOrderService.completeMovementOrder(order.getId());

        // 断言：移库单
        WmsMovementOrderDO dbOrder = movementOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsOrderStatusEnum.FINISHED.getStatus(), dbOrder.getStatus());
        // 断言：库存变更
        ArgumentCaptor<WmsInventoryChangeReqDTO> captor = ArgumentCaptor.forClass(WmsInventoryChangeReqDTO.class);
        verify(inventoryService).changeInventory(captor.capture());
        WmsInventoryChangeReqDTO inventoryReqDTO = captor.getValue();
        assertEquals(order.getId(), inventoryReqDTO.getOrderId());
        assertEquals(order.getNo(), inventoryReqDTO.getOrderNo());
        assertEquals(WmsInventoryOrderTypeEnum.MOVEMENT.getType(), inventoryReqDTO.getOrderType());
        assertEquals(2, inventoryReqDTO.getItems().size());
        assertEquals(skuId, inventoryReqDTO.getItems().get(0).getSkuId());
        assertEquals(sourceWarehouseId, inventoryReqDTO.getItems().get(0).getWarehouseId());
        assertEquals(0, new BigDecimal("-2.00").compareTo(inventoryReqDTO.getItems().get(0).getQuantity()));
        assertEquals(skuId, inventoryReqDTO.getItems().get(1).getSkuId());
        assertEquals(targetWarehouseId, inventoryReqDTO.getItems().get(1).getWarehouseId());
        assertEquals(0, new BigDecimal("2.00").compareTo(inventoryReqDTO.getItems().get(1).getQuantity()));
    }

    @Test
    public void testCompleteMovementOrder_detailRequired() {
        // mock 数据
        WmsMovementOrderDO order = createMovementOrder(100L, 200L);
        movementOrderMapper.insert(order);

        // 调用，并断言
        assertServiceException(() -> movementOrderService.completeMovementOrder(order.getId()),
                MOVEMENT_ORDER_DETAIL_REQUIRED);
        verify(inventoryService, never()).changeInventory(any());
    }

    @Test
    public void testCreateMovementOrder_sameWarehouse() {
        // 准备参数
        WmsMovementOrderSaveReqVO reqVO = createMovementOrderReqVO(100L, 100L);

        // 调用，并断言
        assertServiceException(() -> movementOrderService.createMovementOrder(reqVO), MOVEMENT_ORDER_WAREHOUSE_SAME);
        verify(inventoryService, never()).changeInventory(any());
    }

    @Test
    public void testCancelMovementOrder_success() {
        // mock 数据
        WmsMovementOrderDO order = createMovementOrder(100L, 200L);
        movementOrderMapper.insert(order);

        // 调用
        movementOrderService.cancelMovementOrder(order.getId());

        // 断言
        WmsMovementOrderDO dbOrder = movementOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsOrderStatusEnum.CANCELED.getStatus(), dbOrder.getStatus());
        verify(inventoryService, never()).changeInventory(any());
    }

    @Test
    public void testDeleteMovementOrder_canceled() {
        // mock 数据
        WmsMovementOrderDO order = createMovementOrder(100L, 200L).setStatus(WmsOrderStatusEnum.CANCELED.getStatus());
        movementOrderMapper.insert(order);
        movementOrderDetailMapper.insert(createMovementOrderDetail(order.getId(), 300L, 100L, 200L));

        // 调用
        movementOrderService.deleteMovementOrder(order.getId());

        // 断言
        assertNull(movementOrderMapper.selectById(order.getId()));
        assertEquals(0, movementOrderDetailMapper.selectListByOrderId(order.getId()).size());
    }

    @Test
    public void testDeleteMovementOrder_finished() {
        // mock 数据
        WmsMovementOrderDO order = createMovementOrder(100L, 200L).setStatus(WmsOrderStatusEnum.FINISHED.getStatus());
        movementOrderMapper.insert(order);

        // 调用，并断言
        assertServiceException(() -> movementOrderService.deleteMovementOrder(order.getId()),
                MOVEMENT_ORDER_STATUS_NOT_DELETABLE);
    }

    private static WmsMovementOrderDO createMovementOrder(Long sourceWarehouseId, Long targetWarehouseId) {
        return new WmsMovementOrderDO()
                .setNo("YK202605120001")
                .setStatus(WmsOrderStatusEnum.PREPARE.getStatus())
                .setSourceWarehouseId(sourceWarehouseId)
                .setTargetWarehouseId(targetWarehouseId)
                .setTotalQuantity(new BigDecimal("2.00"))
                .setTotalAmount(new BigDecimal("20.00"));
    }

    private static WmsMovementOrderDetailDO createMovementOrderDetail(Long orderId, Long skuId,
                                                                      Long sourceWarehouseId, Long targetWarehouseId) {
        return WmsMovementOrderDetailDO.builder()
                .orderId(orderId)
                .skuId(skuId)
                .sourceWarehouseId(sourceWarehouseId)
                .targetWarehouseId(targetWarehouseId)
                .quantity(new BigDecimal("2.00"))
                .amount(new BigDecimal("20.00"))
                .build();
    }

    private static WmsMovementOrderSaveReqVO createMovementOrderReqVO(Long sourceWarehouseId, Long targetWarehouseId) {
        WmsMovementOrderSaveReqVO reqVO = new WmsMovementOrderSaveReqVO();
        reqVO.setNo("YK202605120001");
        reqVO.setSourceWarehouseId(sourceWarehouseId);
        reqVO.setTargetWarehouseId(targetWarehouseId);
        reqVO.setTotalQuantity(new BigDecimal("2.00"));
        reqVO.setTotalAmount(new BigDecimal("20.00"));
        return reqVO;
    }

}
