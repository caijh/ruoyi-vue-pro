package cn.iocoder.yudao.module.wms.service.inventory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.WmsInventoryPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryHistoryDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemSkuDO;
import cn.iocoder.yudao.module.wms.dal.mysql.inventory.WmsInventoryHistoryMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.inventory.WmsInventoryMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.md.item.WmsItemMapper;
import cn.iocoder.yudao.module.wms.dal.mysql.md.item.WmsItemSkuMapper;
import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import cn.iocoder.yudao.module.wms.framework.config.WmsProperties;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import({WmsInventoryServiceImpl.class, WmsInventoryDetailServiceImpl.class, WmsInventoryHistoryServiceImpl.class,
        WmsProperties.class})
public class WmsInventoryServiceImplTest extends BaseDbUnitTest {

    @Resource
    private WmsInventoryServiceImpl inventoryService;

    @Resource
    private WmsInventoryMapper inventoryMapper;
    @Resource
    private WmsInventoryHistoryMapper inventoryHistoryMapper;
    @Resource
    private WmsItemMapper itemMapper;
    @Resource
    private WmsItemSkuMapper skuMapper;
    @Resource
    private WmsProperties wmsProperties;

    @Test
    public void testChangeInventory_createInventory() {
        // mock 数据
        wmsProperties.setBatchEnable(false);
        WmsItemDO item = createItem("ITEM-001", "红富士苹果");
        WmsItemSkuDO sku = createSku(item.getId(), "SKU-001", "10kg 箱装");
        WmsInventoryChangeReqDTO reqDTO = createChangeReq(sku.getId(), 100L, 1001L, "5.00");

        // 调用
        inventoryService.changeInventory(reqDTO);

        // 断言：库存余额
        WmsInventoryDO inventory = inventoryMapper.selectBySkuIdAndWarehouseIdAndAreaId(sku.getId(), 100L, 1001L);
        assertNotNull(inventory);
        assertEquals(0, new BigDecimal("5.00").compareTo(inventory.getQuantity()));
        // 断言：库存流水
        List<WmsInventoryHistoryDO> histories = inventoryHistoryMapper.selectList();
        assertEquals(1, histories.size());
        WmsInventoryHistoryDO history = histories.get(0);
        assertEquals(sku.getId(), history.getSkuId());
        assertEquals(100L, history.getWarehouseId());
        assertEquals(1001L, history.getAreaId());
        assertEquals(0, BigDecimal.ZERO.compareTo(history.getBeforeQuantity()));
        assertEquals(0, new BigDecimal("5.00").compareTo(history.getAfterQuantity()));
        assertEquals(reqDTO.getOrderId(), history.getOrderId());
        assertEquals(reqDTO.getOrderNo(), history.getOrderNo());
        assertEquals(reqDTO.getOrderType(), history.getOrderType());
    }

    @Test
    public void testChangeInventory_updateInventory() {
        // mock 数据
        wmsProperties.setBatchEnable(false);
        WmsItemDO item = createItem("ITEM-001", "红富士苹果");
        WmsItemSkuDO sku = createSku(item.getId(), "SKU-001", "10kg 箱装");
        inventoryMapper.insert(createInventory(sku.getId(), 100L, 1001L, "2.00"));
        WmsInventoryChangeReqDTO reqDTO = createChangeReq(sku.getId(), 100L, 1001L, "3.00");

        // 调用
        inventoryService.changeInventory(reqDTO);

        // 断言：库存余额
        WmsInventoryDO inventory = inventoryMapper.selectBySkuIdAndWarehouseIdAndAreaId(sku.getId(), 100L, 1001L);
        assertNotNull(inventory);
        assertEquals(0, new BigDecimal("5.00").compareTo(inventory.getQuantity()));
        // 断言：库存流水
        List<WmsInventoryHistoryDO> histories = inventoryHistoryMapper.selectList();
        assertEquals(1, histories.size());
        WmsInventoryHistoryDO history = histories.get(0);
        assertEquals(0, new BigDecimal("2.00").compareTo(history.getBeforeQuantity()));
        assertEquals(0, new BigDecimal("5.00").compareTo(history.getAfterQuantity()));
        assertEquals(0, new BigDecimal("3.00").compareTo(history.getQuantity()));
    }

    @Test
    public void testGetInventoryPage_groupByWarehouse() {
        // mock 数据
        WmsItemDO item = createItem("ITEM-001", "红富士苹果");
        WmsItemSkuDO sku = createSku(item.getId(), "SKU-001", "10kg 箱装");
        inventoryMapper.insert(createInventory(sku.getId(), 100L, 1001L, "2.00"));
        inventoryMapper.insert(createInventory(sku.getId(), 100L, 1002L, "3.00"));
        inventoryMapper.insert(createInventory(sku.getId(), 200L, 1001L, "5.00"));
        // 准备参数
        WmsInventoryPageReqVO reqVO = new WmsInventoryPageReqVO();
        reqVO.setType(WmsInventoryPageReqVO.TYPE_WAREHOUSE);
        reqVO.setWarehouseId(100L);
        reqVO.setAreaId(1001L); // 仓库维度不按库区过滤

        // 调用
        PageResult<WmsInventoryDO> pageResult = inventoryService.getInventoryPage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        WmsInventoryDO inventory = pageResult.getList().get(0);
        assertEquals(100L, inventory.getWarehouseId());
        assertEquals(0L, inventory.getAreaId());
        assertEquals(sku.getId(), inventory.getSkuId());
        assertEquals(0, new BigDecimal("5.00").compareTo(inventory.getQuantity()));
    }

    @Test
    public void testGetInventoryPage_groupByWarehouse_minQuantity() {
        // mock 数据
        WmsItemDO item = createItem("ITEM-001", "红富士苹果");
        WmsItemSkuDO sku = createSku(item.getId(), "SKU-001", "10kg 箱装");
        inventoryMapper.insert(createInventory(sku.getId(), 100L, 1001L, "2.00"));
        inventoryMapper.insert(createInventory(sku.getId(), 100L, 1002L, "3.00"));
        // 准备参数
        WmsInventoryPageReqVO reqVO = new WmsInventoryPageReqVO();
        reqVO.setType(WmsInventoryPageReqVO.TYPE_WAREHOUSE);
        reqVO.setMinQuantity(new BigDecimal("6.00"));

        // 调用
        PageResult<WmsInventoryDO> pageResult = inventoryService.getInventoryPage(reqVO);
        // 断言
        assertEquals(0, pageResult.getTotal());
        assertEquals(0, pageResult.getList().size());
    }

    @Test
    public void testGetInventoryPage_area() {
        // mock 数据
        WmsItemDO item = createItem("ITEM-001", "红富士苹果");
        WmsItemSkuDO sku = createSku(item.getId(), "SKU-001", "10kg 箱装");
        inventoryMapper.insert(createInventory(sku.getId(), 100L, 1001L, "2.00"));
        inventoryMapper.insert(createInventory(sku.getId(), 100L, 1002L, "3.00"));
        // 准备参数
        WmsInventoryPageReqVO reqVO = new WmsInventoryPageReqVO();
        reqVO.setType(WmsInventoryPageReqVO.TYPE_AREA);
        reqVO.setWarehouseId(100L);

        // 调用
        PageResult<WmsInventoryDO> pageResult = inventoryService.getInventoryPage(reqVO);
        // 断言
        assertEquals(2, pageResult.getTotal());
        assertEquals(2, pageResult.getList().size());
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

    private static WmsInventoryDO createInventory(Long skuId, Long warehouseId, Long areaId, String quantity) {
        return WmsInventoryDO.builder()
                .skuId(skuId)
                .warehouseId(warehouseId)
                .areaId(areaId)
                .quantity(new BigDecimal(quantity))
                .build();
    }

    private static WmsInventoryChangeReqDTO createChangeReq(Long skuId, Long warehouseId, Long areaId, String quantity) {
        return new WmsInventoryChangeReqDTO()
                .setOrderId(1L)
                .setOrderNo("RK202605120001")
                .setOrderType(WmsInventoryOrderTypeEnum.RECEIPT.getType())
                .setItems(List.of(new WmsInventoryChangeReqDTO.Item()
                        .setSkuId(skuId)
                        .setWarehouseId(warehouseId)
                        .setAreaId(areaId)
                        .setQuantity(new BigDecimal(quantity))
                        .setAmount(new BigDecimal("100.00"))
                        .setRemark("测试入库")));
    }

}
