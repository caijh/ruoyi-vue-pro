package cn.iocoder.yudao.module.wms.service.inventory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.detail.WmsInventoryDetailPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDetailDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import cn.iocoder.yudao.module.wms.dal.mysql.inventory.WmsInventoryDetailMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.md.item.WmsItemMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.md.item.WmsItemSkuMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(WmsInventoryDetailServiceImpl.class)
public class WmsInventoryDetailServiceImplTest extends BaseDbUnitTest {

    @Resource
    private WmsInventoryDetailServiceImpl inventoryDetailService;

    @Resource
    private WmsInventoryDetailMapper inventoryDetailMapper;
    @Resource
    private WmsItemMapper itemMapper;
    @Resource
    private WmsItemSkuMapper skuMapper;

    @Test
    public void testGetInventoryDetailPage_daysToExpires() {
        // mock 数据
        WmsItemDO item = createItem("ITEM-001", "红富士苹果");
        WmsItemSkuDO sku = createSku(item.getId(), "SKU-001", "10kg 箱装");
        WmsInventoryDetailDO dbDetail = createInventoryDetail(sku.getId(), 100L, 1001L, "10.00",
                LocalDateTime.of(LocalDate.now().plusDays(10), LocalTime.MIN));
        inventoryDetailMapper.insert(dbDetail);
        inventoryDetailMapper.insert(createInventoryDetail(sku.getId(), 100L, 1002L, "20.00",
                LocalDateTime.of(LocalDate.now().plusDays(40), LocalTime.MIN)));
        // 准备参数
        WmsInventoryDetailPageReqVO reqVO = new WmsInventoryDetailPageReqVO();
        reqVO.setType(WmsInventoryDetailPageReqVO.TYPE_WAREHOUSE);
        reqVO.setDaysToExpires(30);

        // 调用
        PageResult<WmsInventoryDetailDO> pageResult = inventoryDetailService.getInventoryDetailPage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        assertEquals(dbDetail.getId(), pageResult.getList().get(0).getId());
    }

    private WmsItemDO createItem(String code, String name) {
        WmsItemDO item = WmsItemDO.builder()
                .code(code)
                .name(name)
                .unit("箱")
                .categoryId(1L)
                .build();
        itemMapper.insert(item);
        return item;
    }

    private WmsItemSkuDO createSku(Long itemId, String code, String name) {
        WmsItemSkuDO sku = WmsItemSkuDO.builder()
                .itemId(itemId)
                .code(code)
                .name(name)
                .barCode("69010001")
                .build();
        skuMapper.insert(sku);
        return sku;
    }

    private static WmsInventoryDetailDO createInventoryDetail(Long skuId, Long warehouseId, Long areaId,
                                                             String quantity, LocalDateTime expirationDate) {
        return WmsInventoryDetailDO.builder()
                .skuId(skuId)
                .warehouseId(warehouseId)
                .areaId(areaId)
                .quantity(new BigDecimal(quantity))
                .remainQuantity(new BigDecimal(quantity))
                .expirationDate(expirationDate)
                .build();
    }

}
