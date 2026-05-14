package cn.iocoder.yudao.module.wms.service.home;

import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeInventorySummaryRespVO;
import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeOrderSummaryRespVO;
import cn.iocoder.yudao.module.wms.controller.admin.home.vo.WmsHomeOrderTrendRespVO;

import java.util.List;

/**
 * WMS 首页统计 Service 接口
 *
 * @author 芋道源码
 */
public interface WmsHomeStatisticsService {

    /**
     * 获得单据汇总统计
     *
     * @param warehouseId 仓库编号
     * @return 单据汇总统计
     */
    List<WmsHomeOrderSummaryRespVO> getOrderSummary(Long warehouseId);

    /**
     * 获得单据趋势
     *
     * @param days 天数
     * @param warehouseId 仓库编号
     * @return 单据趋势
     */
    List<WmsHomeOrderTrendRespVO> getOrderTrend(Integer days, Long warehouseId);

    /**
     * 获得库存汇总统计
     *
     * @param warehouseId 仓库编号
     * @return 库存汇总统计
     */
    WmsHomeInventorySummaryRespVO getInventorySummary(Long warehouseId);

}
