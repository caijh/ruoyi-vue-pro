package cn.iocoder.yudao.module.wms.service.order.receipt;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt.WmsReceiptOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt.WmsReceiptOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.receipt.WmsReceiptOrderDetailMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.order.receipt.WmsReceiptOrderMapper;
import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsReceiptOrderStatusEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsReceiptOrderTypeEnum;
import cn.iocoder.yudao.module.wms.framework.config.WmsProperties;
import cn.iocoder.yudao.module.wms.service.inventory.WmsInventoryService;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import cn.iocoder.yudao.module.wms.service.md.item.WmsItemSkuService;
import cn.iocoder.yudao.module.wms.service.md.merchant.WmsMerchantService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseAreaService;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@Import({WmsReceiptOrderServiceImpl.class, WmsReceiptOrderDetailServiceImpl.class, WmsProperties.class})
public class WmsReceiptOrderServiceImplTest extends BaseDbUnitTest {

    @Resource
    private WmsReceiptOrderServiceImpl receiptOrderService;

    @Resource
    private WmsReceiptOrderMapper receiptOrderMapper;
    @Resource
    private WmsReceiptOrderDetailMapper receiptOrderDetailMapper;

    @MockitoBean
    private WmsWarehouseService warehouseService;
    @MockitoBean
    private WmsWarehouseAreaService warehouseAreaService;
    @MockitoBean
    private WmsMerchantService merchantService;
    @MockitoBean
    private WmsItemSkuService itemSkuService;
    @MockitoBean
    private WmsInventoryService inventoryService;

    @Test
    public void testCompleteReceiptOrder_success() {
        // mock 数据
        Long warehouseId = 100L;
        Long skuId = 200L;
        WmsReceiptOrderDO order = createReceiptOrder(warehouseId);
        receiptOrderMapper.insert(order);
        receiptOrderDetailMapper.insert(createReceiptOrderDetail(order.getId(), skuId, warehouseId));

        // 调用
        receiptOrderService.completeReceiptOrder(order.getId());

        // 断言：入库单
        WmsReceiptOrderDO dbOrder = receiptOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsReceiptOrderStatusEnum.COMPLETED.getStatus(), dbOrder.getStatus());
        // 断言：库存变更
        ArgumentCaptor<WmsInventoryChangeReqDTO> captor = ArgumentCaptor.forClass(WmsInventoryChangeReqDTO.class);
        verify(inventoryService).changeInventory(captor.capture());
        WmsInventoryChangeReqDTO inventoryReqDTO = captor.getValue();
        assertEquals(order.getId(), inventoryReqDTO.getOrderId());
        assertEquals(order.getNo(), inventoryReqDTO.getOrderNo());
        assertEquals(WmsInventoryOrderTypeEnum.RECEIPT.getType(), inventoryReqDTO.getOrderType());
        assertEquals(1, inventoryReqDTO.getItems().size());
        assertEquals(skuId, inventoryReqDTO.getItems().get(0).getSkuId());
        assertEquals(warehouseId, inventoryReqDTO.getItems().get(0).getWarehouseId());
        assertEquals(0, new BigDecimal("2.00").compareTo(inventoryReqDTO.getItems().get(0).getQuantity()));
    }

    @Test
    public void testCancelReceiptOrder_success() {
        // mock 数据
        WmsReceiptOrderDO order = createReceiptOrder(100L);
        receiptOrderMapper.insert(order);

        // 调用
        receiptOrderService.cancelReceiptOrder(order.getId());

        // 断言
        WmsReceiptOrderDO dbOrder = receiptOrderMapper.selectById(order.getId());
        assertNotNull(dbOrder);
        assertEquals(WmsReceiptOrderStatusEnum.CANCELED.getStatus(), dbOrder.getStatus());
        verify(inventoryService, never()).changeInventory(any());
    }

    private static WmsReceiptOrderDO createReceiptOrder(Long warehouseId) {
        return new WmsReceiptOrderDO()
                .setNo("RK202605120001")
                .setType(WmsReceiptOrderTypeEnum.PURCHASE.getType())
                .setStatus(WmsReceiptOrderStatusEnum.PENDING.getStatus())
                .setWarehouseId(warehouseId)
                .setAreaId(0L)
                .setTotalQuantity(new BigDecimal("2.00"))
                .setTotalAmount(new BigDecimal("20.00"));
    }

    private static WmsReceiptOrderDetailDO createReceiptOrderDetail(Long orderId, Long skuId, Long warehouseId) {
        return WmsReceiptOrderDetailDO.builder()
                .orderId(orderId)
                .skuId(skuId)
                .warehouseId(warehouseId)
                .areaId(0L)
                .quantity(new BigDecimal("2.00"))
                .amount(new BigDecimal("20.00"))
                .build();
    }

}
