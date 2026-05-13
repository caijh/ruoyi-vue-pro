package cn.iocoder.yudao.module.wms.service.inventory;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.detail.WmsInventoryDetailPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.inventory.WmsInventoryDetailMapper;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.INVENTORY_DETAIL_NOT_EXISTS;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.INVENTORY_DETAIL_REMAIN_QUANTITY_NOT_ENOUGH;

/**
 * WMS 库存明细 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsInventoryDetailServiceImpl implements WmsInventoryDetailService {

    @Resource
    private WmsInventoryDetailMapper inventoryDetailMapper;

    @Override
    public PageResult<WmsInventoryDetailDO> getInventoryDetailPage(WmsInventoryDetailPageReqVO pageReqVO) {
        return inventoryDetailMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createInventoryDetailList(List<WmsInventoryDetailDO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        inventoryDetailMapper.insertBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decreaseInventoryDetailList(List<WmsInventoryChangeReqDTO.Item> items) {
        if (CollUtil.isEmpty(items)) {
            return;
        }
        if (items.stream().anyMatch(item -> item.getInventoryDetailId() == null)) {
            throw exception(INVENTORY_DETAIL_NOT_EXISTS);
        }
        List<WmsInventoryChangeReqDTO.Item> sortedItems = items.stream()
                .sorted(Comparator.comparing(WmsInventoryChangeReqDTO.Item::getInventoryDetailId))
                .toList();

        // 1. 按库存明细编号固定顺序加锁，避免多明细并发扣减时交叉拿锁
        Map<Long, WmsInventoryDetailDO> detailMap = convertMap(inventoryDetailMapper.selectListByIdsForUpdate(
                convertSet(sortedItems, WmsInventoryChangeReqDTO.Item::getInventoryDetailId)),
                WmsInventoryDetailDO::getId);

        // 2. 校验库存明细归属，并合并相同库存明细的扣减数量
        Map<Long, BigDecimal> decreaseQuantityMap = new LinkedHashMap<>();
        for (WmsInventoryChangeReqDTO.Item item : sortedItems) {
            WmsInventoryDetailDO detail = detailMap.get(item.getInventoryDetailId());
            if (detail == null
                    || ObjectUtil.notEqual(detail.getSkuId(), item.getSkuId())
                    || ObjectUtil.notEqual(detail.getWarehouseId(), item.getWarehouseId())
                    || ObjectUtil.notEqual(detail.getAreaId(), item.getAreaId())) {
                throw exception(INVENTORY_DETAIL_NOT_EXISTS);
            }
            decreaseQuantityMap.merge(detail.getId(), item.getQuantity().abs(), BigDecimal::add);
        }

        // 3. 校验并扣减库存明细剩余数量
        for (Map.Entry<Long, BigDecimal> entry : decreaseQuantityMap.entrySet()) {
            WmsInventoryDetailDO detail = detailMap.get(entry.getKey());
            BigDecimal decreaseQuantity = entry.getValue();
            if (detail.getRemainQuantity().compareTo(decreaseQuantity) < 0) {
                throw exception(INVENTORY_DETAIL_REMAIN_QUANTITY_NOT_ENOUGH,
                        detail.getRemainQuantity(), decreaseQuantity);
            }
            inventoryDetailMapper.updateRemainQuantityIncr(detail.getId(), decreaseQuantity.negate());
        }
    }

}
