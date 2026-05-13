package cn.iocoder.yudao.module.wms.service.order.check;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.check.WmsCheckOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.check.WmsCheckOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.check.WmsCheckOrderDetailMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.order.check.WmsCheckOrderMapper;
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
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.CHECK_ORDER_DETAIL_REQUIRED;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.CHECK_ORDER_STATUS_NOT_DELETABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Import({WmsCheckOrderServiceImpl.class, WmsCheckOrderDetailServiceImpl.class})
public class WmsCheckOrderServiceImplTest extends BaseDbUnitTest {

    @Resource
    private WmsCheckOrderServiceImpl checkOrderService;

    @Resource
    private WmsCheckOrderMapper checkOrderMapper;
    @Resource
    private WmsCheckOrderDetailMapper checkOrderDetailMapper;

    @MockitoBean
    private WmsWarehouseService warehouseService;
    @MockitoBean
    private WmsItemSkuService itemSkuService;
    @MockitoBean
    private WmsInventoryService inventoryService;

    @Test
    public void testCompleteCheckOrder_success() {
        // mock 数据
        Long warehouseId = 100L;
        Long skuId = 200L;
        WmsCheckOrderDO order = createCheckOrder(warehouseId);
        checkOrderMapper.insert(order);
        checkOrderDetailMapper.insert(createCheckOrderDetail(order.getId(), skuId, warehouseId,
                "10.00", "7.00"));

        // 调用
        checkOrderService.completeCheckOrder(order.getId());

        // 断言：盘库单
        WmsCheckOrderDO dbOrder = checkOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsOrderStatusEnum.FINISHED.getStatus(), dbOrder.getStatus());
        // 断言：库存变更
        ArgumentCaptor<WmsInventoryChangeReqDTO> captor = ArgumentCaptor.forClass(WmsInventoryChangeReqDTO.class);
        verify(inventoryService).changeInventory(captor.capture());
        WmsInventoryChangeReqDTO inventoryReqDTO = captor.getValue();
        assertEquals(order.getId(), inventoryReqDTO.getOrderId());
        assertEquals(order.getNo(), inventoryReqDTO.getOrderNo());
        assertEquals(WmsInventoryOrderTypeEnum.CHECK.getType(), inventoryReqDTO.getOrderType());
        assertEquals(1, inventoryReqDTO.getItems().size());
        assertEquals(skuId, inventoryReqDTO.getItems().get(0).getSkuId());
        assertEquals(warehouseId, inventoryReqDTO.getItems().get(0).getWarehouseId());
        assertEquals(0, new BigDecimal("-3.00").compareTo(inventoryReqDTO.getItems().get(0).getQuantity()));
    }

    @Test
    public void testCompleteCheckOrder_zeroDifference() {
        // mock 数据
        WmsCheckOrderDO order = createCheckOrder(100L);
        checkOrderMapper.insert(order);
        checkOrderDetailMapper.insert(createCheckOrderDetail(order.getId(), 200L, 100L,
                "10.00", "10.00"));

        // 调用
        checkOrderService.completeCheckOrder(order.getId());

        // 断言
        WmsCheckOrderDO dbOrder = checkOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsOrderStatusEnum.FINISHED.getStatus(), dbOrder.getStatus());
        verify(inventoryService, never()).changeInventory(any());
    }

    @Test
    public void testCompleteCheckOrder_detailRequired() {
        // mock 数据
        WmsCheckOrderDO order = createCheckOrder(100L);
        checkOrderMapper.insert(order);

        // 调用，并断言
        assertServiceException(() -> checkOrderService.completeCheckOrder(order.getId()),
                CHECK_ORDER_DETAIL_REQUIRED);
        verify(inventoryService, never()).changeInventory(any());
    }

    @Test
    public void testCancelCheckOrder_success() {
        // mock 数据
        WmsCheckOrderDO order = createCheckOrder(100L);
        checkOrderMapper.insert(order);

        // 调用
        checkOrderService.cancelCheckOrder(order.getId());

        // 断言
        WmsCheckOrderDO dbOrder = checkOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsOrderStatusEnum.CANCELED.getStatus(), dbOrder.getStatus());
        verify(inventoryService, never()).changeInventory(any());
    }

    @Test
    public void testDeleteCheckOrder_canceled() {
        // mock 数据
        WmsCheckOrderDO order = createCheckOrder(100L).setStatus(WmsOrderStatusEnum.CANCELED.getStatus());
        checkOrderMapper.insert(order);
        checkOrderDetailMapper.insert(createCheckOrderDetail(order.getId(), 200L, 100L,
                "10.00", "8.00"));

        // 调用
        checkOrderService.deleteCheckOrder(order.getId());

        // 断言
        assertNull(checkOrderMapper.selectById(order.getId()));
        assertEquals(0, checkOrderDetailMapper.selectListByOrderId(order.getId()).size());
    }

    @Test
    public void testDeleteCheckOrder_finished() {
        // mock 数据
        WmsCheckOrderDO order = createCheckOrder(100L).setStatus(WmsOrderStatusEnum.FINISHED.getStatus());
        checkOrderMapper.insert(order);

        // 调用，并断言
        assertServiceException(() -> checkOrderService.deleteCheckOrder(order.getId()),
                CHECK_ORDER_STATUS_NOT_DELETABLE);
    }

    private static WmsCheckOrderDO createCheckOrder(Long warehouseId) {
        return new WmsCheckOrderDO()
                .setNo("PK202605120001")
                .setStatus(WmsOrderStatusEnum.PREPARE.getStatus())
                .setWarehouseId(warehouseId)
                .setTotalQuantity(new BigDecimal("-3.00"))
                .setTotalAmount(new BigDecimal("30.00"));
    }

    private static WmsCheckOrderDetailDO createCheckOrderDetail(Long orderId, Long skuId, Long warehouseId,
                                                               String quantity, String checkQuantity) {
        return WmsCheckOrderDetailDO.builder()
                .orderId(orderId)
                .skuId(skuId)
                .warehouseId(warehouseId)
                .inventoryId(300L)
                .quantity(new BigDecimal(quantity))
                .checkQuantity(new BigDecimal(checkQuantity))
                .amount(new BigDecimal("30.00"))
                .build();
    }

}
