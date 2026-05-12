package cn.iocoder.yudao.module.wms.service.order.receipt;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt.WmsReceiptOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.receipt.WmsReceiptOrderDetailMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.RECEIPT_ORDER_DETAIL_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Import(WmsReceiptOrderDetailServiceImpl.class)
public class WmsReceiptOrderDetailServiceImplTest extends BaseDbUnitTest {

    @Resource
    private WmsReceiptOrderDetailServiceImpl receiptOrderDetailService;

    @Resource
    private WmsReceiptOrderDetailMapper receiptOrderDetailMapper;

    @Test
    public void testCreateReceiptOrderDetailList_success() {
        // mock 数据
        Long orderId = 10L;
        WmsReceiptOrderDetailDO detail01 = createReceiptOrderDetail(null, 1001L, "1.00");
        WmsReceiptOrderDetailDO detail02 = createReceiptOrderDetail(null, 1002L, "2.00");

        // 调用
        receiptOrderDetailService.createReceiptOrderDetailList(orderId, List.of(detail01, detail02));

        // 断言
        List<WmsReceiptOrderDetailDO> details = receiptOrderDetailMapper.selectListByOrderId(orderId);
        assertEquals(2, details.size());
        assertEquals(orderId, details.get(0).getOrderId());
        assertEquals(orderId, details.get(1).getOrderId());
    }

    @Test
    public void testCreateReceiptOrderDetailList_detailNotExists() {
        // mock 数据
        WmsReceiptOrderDetailDO detail = createReceiptOrderDetail(null, 1001L, "1.00");
        detail.setId(999L);

        // 调用，并断言
        assertServiceException(() -> receiptOrderDetailService.createReceiptOrderDetailList(10L, List.of(detail)),
                RECEIPT_ORDER_DETAIL_NOT_EXISTS);
    }

    @Test
    public void testUpdateReceiptOrderDetailList_diff() {
        // mock 数据
        Long orderId = 10L;
        WmsReceiptOrderDetailDO detail01 = createReceiptOrderDetail(orderId, 1001L, "1.00");
        WmsReceiptOrderDetailDO detail02 = createReceiptOrderDetail(orderId, 1002L, "2.00");
        receiptOrderDetailMapper.insert(detail01);
        receiptOrderDetailMapper.insert(detail02);
        WmsReceiptOrderDetailDO updateDetail = createReceiptOrderDetail(999L, 2001L, "11.00");
        updateDetail.setId(detail01.getId());
        WmsReceiptOrderDetailDO createDetail = createReceiptOrderDetail(null, 2002L, "22.00");

        // 调用
        receiptOrderDetailService.updateReceiptOrderDetailList(orderId, List.of(updateDetail, createDetail));

        // 断言：修改
        WmsReceiptOrderDetailDO dbUpdateDetail = receiptOrderDetailMapper.selectById(detail01.getId());
        assertNotNull(dbUpdateDetail);
        assertEquals(orderId, dbUpdateDetail.getOrderId());
        assertEquals(2001L, dbUpdateDetail.getSkuId());
        assertEquals(0, new BigDecimal("11.00").compareTo(dbUpdateDetail.getQuantity()));
        // 断言：新增
        List<WmsReceiptOrderDetailDO> details = receiptOrderDetailMapper.selectListByOrderId(orderId);
        assertEquals(2, details.size());
        WmsReceiptOrderDetailDO dbCreateDetail = details.stream()
                .filter(detail -> Long.valueOf(2002L).equals(detail.getSkuId()))
                .findFirst().orElse(null);
        assertNotNull(dbCreateDetail);
        assertEquals(orderId, dbCreateDetail.getOrderId());
        assertEquals(0, new BigDecimal("22.00").compareTo(dbCreateDetail.getQuantity()));
        // 断言：删除
        assertNull(receiptOrderDetailMapper.selectById(detail02.getId()));
    }

    @Test
    public void testUpdateReceiptOrderDetailList_detailNotExists() {
        // mock 数据
        WmsReceiptOrderDetailDO detail = createReceiptOrderDetail(10L, 1001L, "1.00");
        detail.setId(999L);

        // 调用，并断言
        assertServiceException(() -> receiptOrderDetailService.updateReceiptOrderDetailList(10L, List.of(detail)),
                RECEIPT_ORDER_DETAIL_NOT_EXISTS);
    }

    private static WmsReceiptOrderDetailDO createReceiptOrderDetail(Long orderId, Long skuId, String quantity) {
        return WmsReceiptOrderDetailDO.builder()
                .orderId(orderId)
                .skuId(skuId)
                .warehouseId(100L)
                .areaId(0L)
                .quantity(new BigDecimal(quantity))
                .amount(new BigDecimal("100.00"))
                .build();
    }

}
