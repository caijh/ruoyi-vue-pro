package cn.iocoder.yudao.module.wms.service.order.shipment;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.shipment.WmsShipmentOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.shipment.WmsShipmentOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.shipment.WmsShipmentOrderDetailMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.order.shipment.WmsShipmentOrderMapper;
import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsOrderStatusEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsShipmentOrderTypeEnum;
import cn.iocoder.yudao.module.wms.service.inventory.WmsInventoryService;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemSkuService;
import cn.iocoder.yudao.module.wms.service.md.merchant.WmsMerchantService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.SHIPMENT_ORDER_DETAIL_REQUIRED;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.SHIPMENT_ORDER_STATUS_NOT_DELETABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Import({WmsShipmentOrderServiceImpl.class, WmsShipmentOrderDetailServiceImpl.class})
public class WmsShipmentOrderServiceImplTest extends BaseDbUnitTest {

    @Resource
    private WmsShipmentOrderServiceImpl shipmentOrderService;

    @Resource
    private WmsShipmentOrderMapper shipmentOrderMapper;
    @Resource
    private WmsShipmentOrderDetailMapper shipmentOrderDetailMapper;

    @MockitoBean
    private WmsWarehouseService warehouseService;
    @MockitoBean
    private WmsMerchantService merchantService;
    @MockitoBean
    private WmsItemSkuService itemSkuService;
    @MockitoBean
    private WmsInventoryService inventoryService;

    @Test
    public void testCompleteShipmentOrder_success() {
        // mock 数据
        Long warehouseId = 100L;
        Long skuId = 200L;
        WmsShipmentOrderDO order = createShipmentOrder(warehouseId);
        shipmentOrderMapper.insert(order);
        shipmentOrderDetailMapper.insert(createShipmentOrderDetail(order.getId(), skuId, warehouseId));

        // 调用
        shipmentOrderService.completeShipmentOrder(order.getId());

        // 断言：出库单
        WmsShipmentOrderDO dbOrder = shipmentOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsOrderStatusEnum.FINISHED.getStatus(), dbOrder.getStatus());
        // 断言：库存变更
        ArgumentCaptor<WmsInventoryChangeReqDTO> captor = ArgumentCaptor.forClass(WmsInventoryChangeReqDTO.class);
        verify(inventoryService).changeInventory(captor.capture());
        WmsInventoryChangeReqDTO inventoryReqDTO = captor.getValue();
        assertEquals(order.getId(), inventoryReqDTO.getOrderId());
        assertEquals(order.getNo(), inventoryReqDTO.getOrderNo());
        assertEquals(WmsInventoryOrderTypeEnum.SHIPMENT.getType(), inventoryReqDTO.getOrderType());
        assertEquals(1, inventoryReqDTO.getItems().size());
        assertEquals(skuId, inventoryReqDTO.getItems().get(0).getSkuId());
        assertEquals(warehouseId, inventoryReqDTO.getItems().get(0).getWarehouseId());
        assertEquals(0, new BigDecimal("-2.00").compareTo(inventoryReqDTO.getItems().get(0).getQuantity()));
    }

    @Test
    public void testCompleteShipmentOrder_detailRequired() {
        // mock 数据
        WmsShipmentOrderDO order = createShipmentOrder(100L);
        shipmentOrderMapper.insert(order);

        // 调用，并断言
        assertServiceException(() -> shipmentOrderService.completeShipmentOrder(order.getId()),
                SHIPMENT_ORDER_DETAIL_REQUIRED);
        verify(inventoryService, never()).changeInventory(any());
    }

    @Test
    public void testCancelShipmentOrder_success() {
        // mock 数据
        WmsShipmentOrderDO order = createShipmentOrder(100L);
        shipmentOrderMapper.insert(order);

        // 调用
        shipmentOrderService.cancelShipmentOrder(order.getId());

        // 断言
        WmsShipmentOrderDO dbOrder = shipmentOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsOrderStatusEnum.CANCELED.getStatus(), dbOrder.getStatus());
        verify(inventoryService, never()).changeInventory(any());
    }

    @Test
    public void testDeleteShipmentOrder_canceled() {
        // mock 数据
        WmsShipmentOrderDO order = createShipmentOrder(100L).setStatus(WmsOrderStatusEnum.CANCELED.getStatus());
        shipmentOrderMapper.insert(order);
        shipmentOrderDetailMapper.insert(createShipmentOrderDetail(order.getId(), 200L, 100L));

        // 调用
        shipmentOrderService.deleteShipmentOrder(order.getId());

        // 断言
        assertNull(shipmentOrderMapper.selectById(order.getId()));
        assertEquals(0, shipmentOrderDetailMapper.selectListByOrderId(order.getId()).size());
    }

    @Test
    public void testDeleteShipmentOrder_finished() {
        // mock 数据
        WmsShipmentOrderDO order = createShipmentOrder(100L).setStatus(WmsOrderStatusEnum.FINISHED.getStatus());
        shipmentOrderMapper.insert(order);

        // 调用，并断言
        assertServiceException(() -> shipmentOrderService.deleteShipmentOrder(order.getId()),
                SHIPMENT_ORDER_STATUS_NOT_DELETABLE);
    }

    private static WmsShipmentOrderDO createShipmentOrder(Long warehouseId) {
        return new WmsShipmentOrderDO()
                .setNo("CK202605120001")
                .setType(WmsShipmentOrderTypeEnum.SALE.getType())
                .setOrderTime(LocalDateTime.of(2026, 5, 12, 0, 0))
                .setStatus(WmsOrderStatusEnum.PREPARE.getStatus())
                .setWarehouseId(warehouseId)
                .setTotalQuantity(new BigDecimal("2.00"))
                .setTotalAmount(new BigDecimal("20.00"));
    }

    private static WmsShipmentOrderDetailDO createShipmentOrderDetail(Long orderId, Long skuId, Long warehouseId) {
        return WmsShipmentOrderDetailDO.builder()
                .orderId(orderId)
                .skuId(skuId)
                .warehouseId(warehouseId)
                .quantity(new BigDecimal("2.00"))
                .amount(new BigDecimal("20.00"))
                .build();
    }

}
