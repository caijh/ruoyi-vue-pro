package cn.iocoder.yudao.module.wms.service.order.receipt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.receipt.WmsReceiptOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.receipt.WmsReceiptOrderDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.RECEIPT_ORDER_DETAIL_NOT_EXISTS;

/**
 * WMS 入库单明细 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsReceiptOrderDetailServiceImpl implements WmsReceiptOrderDetailService {

    @Resource
    private WmsReceiptOrderDetailMapper receiptOrderDetailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReceiptOrderDetailList(Long orderId, List<WmsReceiptOrderDetailDO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        if (CollUtil.isNotEmpty(convertList(list, WmsReceiptOrderDetailDO::getId))) {
            throw exception(RECEIPT_ORDER_DETAIL_NOT_EXISTS);
        }
        list.forEach(detail -> detail.setOrderId(orderId));
        receiptOrderDetailMapper.insertBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReceiptOrderDetailList(Long orderId, List<WmsReceiptOrderDetailDO> list) {
        // 第一步，对比新老数据，获得添加、修改、删除的列表
        List<WmsReceiptOrderDetailDO> oldList = receiptOrderDetailMapper.selectListByOrderId(orderId);
        List<WmsReceiptOrderDetailDO> newList = CollUtil.isEmpty(list) ? ListUtil.of() : list;
        List<List<WmsReceiptOrderDetailDO>> diffList = diffList(oldList, newList, // id 不同，就认为是不同的记录
                (oldVal, newVal) -> oldVal.getId().equals(newVal.getId()));

        // 第二步，批量添加、修改、删除
        if (CollUtil.isNotEmpty(diffList.get(0))) {
            if (CollUtil.isNotEmpty(convertList(diffList.get(0), WmsReceiptOrderDetailDO::getId))) {
                throw exception(RECEIPT_ORDER_DETAIL_NOT_EXISTS);
            }
            diffList.get(0).forEach(detail -> detail.setOrderId(orderId));
            receiptOrderDetailMapper.insertBatch(diffList.get(0));
        }
        if (CollUtil.isNotEmpty(diffList.get(1))) {
            diffList.get(1).forEach(detail -> detail.setOrderId(orderId));
            receiptOrderDetailMapper.updateBatch(diffList.get(1));
        }
        if (CollUtil.isNotEmpty(diffList.get(2))) {
            receiptOrderDetailMapper.deleteByIds(convertList(diffList.get(2), WmsReceiptOrderDetailDO::getId));
        }
    }

    @Override
    public void deleteReceiptOrderDetailListByOrderId(Long orderId) {
        receiptOrderDetailMapper.deleteByOrderId(orderId);
    }

    @Override
    public List<WmsReceiptOrderDetailDO> getReceiptOrderDetailList(Long orderId) {
        return receiptOrderDetailMapper.selectListByOrderId(orderId);
    }

    @Override
    public List<WmsReceiptOrderDetailDO> getReceiptOrderDetailList(Collection<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return ListUtil.of();
        }
        return receiptOrderDetailMapper.selectListByOrderIds(orderIds);
    }

}
