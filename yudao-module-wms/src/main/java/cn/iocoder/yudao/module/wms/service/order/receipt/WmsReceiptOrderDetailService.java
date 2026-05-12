package cn.iocoder.yudao.module.wms.service.order.receipt;

import cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt.WmsReceiptOrderDetailDO;

import java.util.Collection;
import java.util.List;

/**
 * WMS 入库单明细 Service 接口
 *
 * @author 芋道源码
 */
public interface WmsReceiptOrderDetailService {

    /**
     * 创建入库单明细列表
     *
     * @param orderId 入库单编号
     * @param list 明细列表
     */
    void createReceiptOrderDetailList(Long orderId, List<WmsReceiptOrderDetailDO> list);

    /**
     * 更新入库单明细列表
     *
     * @param orderId 入库单编号
     * @param list 明细列表
     */
    void updateReceiptOrderDetailList(Long orderId, List<WmsReceiptOrderDetailDO> list);

    /**
     * 按入库单编号删除明细列表
     *
     * @param orderId 入库单编号
     */
    void deleteReceiptOrderDetailListByOrderId(Long orderId);

    /**
     * 按入库单编号获得明细列表
     *
     * @param orderId 入库单编号
     * @return 明细列表
     */
    List<WmsReceiptOrderDetailDO> getReceiptOrderDetailList(Long orderId);

    /**
     * 按入库单编号集合获得明细列表
     *
     * @param orderIds 入库单编号集合
     * @return 明细列表
     */
    List<WmsReceiptOrderDetailDO> getReceiptOrderDetailList(Collection<Long> orderIds);

}
