package cn.iocoder.yudao.module.wms.service.home;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeInventoryItemRankRespVO;
import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeInventorySummaryRespVO;
import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeInventoryWarehouseRankRespVO;
import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeOrderStatusRespVO;
import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeOrderSummaryRespVO;
import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeOrderTrendRespVO;
import cn.iocoder.yudao.module.wms.dal.mysql.home.WmsHomeStatisticsMapper;
import cn.iocoder.yudao.module.wms.enums.order.WmsOrderStatusEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsOrderTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WMS 首页统计 Service 实现类
 *
 * @author 芋道源码
 */
@Service
public class WmsHomeStatisticsServiceImpl implements WmsHomeStatisticsService {

    // TODO @AI：8、5、交给前端传递；
    private static final int GOODS_SHARE_LIMIT = 5;
    private static final int WAREHOUSE_DISTRIBUTION_LIMIT = 8;

    @Resource
    private WmsHomeStatisticsMapper homeStatisticsMapper;

    @Override
    public List<WmsHomeOrderSummaryRespVO> getOrderSummary(Long warehouseId) {
        List<Map<String, Object>> stats = homeStatisticsMapper.selectOrderCountGroupByTypeAndStatus(warehouseId);
        // TODO @AI：convertList 链式调用。
        List<WmsHomeOrderSummaryRespVO> result = new ArrayList<>();
        for (WmsOrderTypeEnum orderTypeEnum : WmsOrderTypeEnum.values()) {
            WmsHomeOrderSummaryRespVO vo = new WmsHomeOrderSummaryRespVO()
                    .setOrderType(orderTypeEnum.getType()).setOrderTypeName(orderTypeEnum.getName());
            // TODO @AI：collutils sum 是不是可以？
            long total = 0L;
            List<WmsHomeOrderStatusRespVO> statusList = new ArrayList<>();
            for (WmsOrderStatusEnum statusEnum : WmsOrderStatusEnum.values()) {
                long count = getOrderCount(stats, orderTypeEnum.getType(), statusEnum.getStatus());
                total += count;
                statusList.add(new WmsHomeOrderStatusRespVO(statusEnum.getStatus(), statusEnum.getName(), count));
            }
            vo.setTotal(total).setStatusList(statusList);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<WmsHomeOrderTrendRespVO> getOrderTrend(Integer days, Long warehouseId) {
        // TODO @AI：同上？
        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = endDate.minusDays(days);
        LocalDateTime beginTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atStartOfDay();

        List<Map<String, Object>> dbData = homeStatisticsMapper.selectDailyOrderTrend(beginTime, endTime, warehouseId);
        Map<String, Map<Integer, Long>> dateMap = new LinkedHashMap<>();
        for (Map<String, Object> row : dbData) {
            String date = (String) row.get("date");
            Integer orderType = getInteger(row, "orderType");
            Long count = getLong(row, "count");
            dateMap.computeIfAbsent(date, key -> new HashMap<>()).put(orderType, count);
        }
        // TODO @AI：链式调用；
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<WmsHomeOrderTrendRespVO> result = new ArrayList<>();
        for (LocalDate d = startDate; d.isBefore(endDate); d = d.plusDays(1)) {
            String dateStr = d.format(fmt);
            Map<Integer, Long> row = dateMap.getOrDefault(dateStr, Collections.emptyMap());
            WmsHomeOrderTrendRespVO vo = new WmsHomeOrderTrendRespVO();
            vo.setDate(dateStr);
            vo.setReceiptCount(row.getOrDefault(WmsOrderTypeEnum.RECEIPT.getType(), 0L));
            vo.setShipmentCount(row.getOrDefault(WmsOrderTypeEnum.SHIPMENT.getType(), 0L));
            vo.setMovementCount(row.getOrDefault(WmsOrderTypeEnum.MOVEMENT.getType(), 0L));
            vo.setCheckCount(row.getOrDefault(WmsOrderTypeEnum.CHECK.getType(), 0L));
            result.add(vo);
        }
        return result;
    }

    @Override
    public WmsHomeInventorySummaryRespVO getInventorySummary(Long warehouseId) {
        // TODO @AI：要不都先查询；然后 set 设置值；
        return new WmsHomeInventorySummaryRespVO()
                .setTotalQuantity(getDecimal(homeStatisticsMapper.selectInventoryTotalQuantity(warehouseId)))
                .setGoodsShareList(buildInventoryItemRankList(homeStatisticsMapper.selectInventoryItemRank(warehouseId, GOODS_SHARE_LIMIT)))
                .setWarehouseDistributionList(buildInventoryWarehouseRankList(homeStatisticsMapper.selectInventoryWarehouseRank(warehouseId, WAREHOUSE_DISTRIBUTION_LIMIT)));
    }

    // TODO @AI：这个 ===== 太短了，加长！
    // ========== 工具方法 ==========

    private long getOrderCount(List<Map<String, Object>> stats, Integer orderType, Integer status) {
        for (Map<String, Object> stat : stats) {
            if (ObjectUtil.equal(getInteger(stat, "orderType"), orderType)
                    && ObjectUtil.equal(getInteger(stat, "status"), status)) {
                return getLong(stat, "count");
            }
        }
        return 0L;
    }

    // TODO @AI：convertList；然后链式调用；
    private List<WmsHomeInventoryItemRankRespVO> buildInventoryItemRankList(List<Map<String, Object>> rows) {
        List<WmsHomeInventoryItemRankRespVO> result = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            WmsHomeInventoryItemRankRespVO vo = new WmsHomeInventoryItemRankRespVO();
            vo.setItemId(getLong(row, "itemId"));
            vo.setItemName((String) row.get("itemName"));
            vo.setQuantity(getDecimal(row.get("quantity")));
            result.add(vo);
        }
        return result;
    }

    // TODO @AI：convertList；然后链式调用；
    private List<WmsHomeInventoryWarehouseRankRespVO> buildInventoryWarehouseRankList(List<Map<String, Object>> rows) {
        List<WmsHomeInventoryWarehouseRankRespVO> result = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            WmsHomeInventoryWarehouseRankRespVO vo = new WmsHomeInventoryWarehouseRankRespVO();
            vo.setWarehouseId(getLong(row, "warehouseId"));
            vo.setWarehouseName((String) row.get("warehouseName"));
            vo.setQuantity(getDecimal(row.get("quantity")));
            result.add(vo);
        }
        return result;
    }

    // TODO @AI：maputil 直接使用
    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? null : ((Number) value).intValue();
    }

    // TODO @AI：maputil 直接使用
    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value == null ? 0L : ((Number) value).longValue();
    }

    // TODO @AI：maputil 直接使用
    private BigDecimal getDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        return new BigDecimal(value.toString());
    }

}
