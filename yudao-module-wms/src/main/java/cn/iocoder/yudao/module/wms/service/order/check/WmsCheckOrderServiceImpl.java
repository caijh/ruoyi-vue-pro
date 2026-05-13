package cn.iocoder.yudao.module.wms.service.order.check;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.wms.controller.admin.order.check.vo.order.WmsCheckOrderPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.order.check.vo.order.WmsCheckOrderSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.check.WmsCheckOrderDO;
import cn.iocoder.yudao.module.wms.dal.dataobject.order.check.WmsCheckOrderDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.order.check.WmsCheckOrderMapper;
import cn.iocoder.yudao.module.wms.enums.inventory.WmsInventoryOrderTypeEnum;
import cn.iocoder.yudao.module.wms.enums.order.WmsOrderStatusEnum;
import cn.iocoder.yudao.module.wms.service.inventory.WmsInventoryService;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;
import cn.iocoder.yudao.module.wms.service.md.warehouse.WmsWarehouseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * WMS 盘库单 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsCheckOrderServiceImpl implements WmsCheckOrderService {

    @Resource
    private WmsCheckOrderMapper checkOrderMapper;
    @Resource
    private WmsCheckOrderDetailService checkOrderDetailService;
    @Resource
    private WmsWarehouseService warehouseService;
    @Resource
    private WmsInventoryService inventoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCheckOrder(WmsCheckOrderSaveReqVO createReqVO) {
        // 1. 校验盘库单保存数据
        validateCheckOrderSaveData(createReqVO);

        // 2.1 插入盘库单
        WmsCheckOrderDO order = BeanUtils.toBean(createReqVO, WmsCheckOrderDO.class);
        order.setStatus(WmsOrderStatusEnum.PREPARE.getStatus());
        checkOrderMapper.insert(order);
        // 2.2 插入盘库单明细
        checkOrderDetailService.createCheckOrderDetailList(order.getId(), createReqVO);
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCheckOrder(WmsCheckOrderSaveReqVO updateReqVO) {
        // 1. 校验盘库单保存数据
        validateCheckOrderPrepare(updateReqVO.getId());
        validateCheckOrderSaveData(updateReqVO);

        // 2.1 更新盘库单
        WmsCheckOrderDO updateObj = BeanUtils.toBean(updateReqVO, WmsCheckOrderDO.class)
                .setStatus(WmsOrderStatusEnum.PREPARE.getStatus());
        checkOrderMapper.updateById(updateObj);
        // 2.2 更新盘库单明细
        checkOrderDetailService.updateCheckOrderDetailList(updateReqVO.getId(), updateReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCheckOrder(Long id) {
        // 1. 校验存在，且可删除
        WmsCheckOrderDO order = validateCheckOrderExists(id);
        if (ObjectUtil.notEqual(order.getStatus(), WmsOrderStatusEnum.PREPARE.getStatus())
                && ObjectUtil.notEqual(order.getStatus(), WmsOrderStatusEnum.CANCELED.getStatus())) {
            throw exception(CHECK_ORDER_STATUS_NOT_DELETABLE);
        }

        // 2.1 删除盘库单
        checkOrderMapper.deleteById(id);
        // 2.2 删除盘库单明细
        checkOrderDetailService.deleteCheckOrderDetailListByOrderId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeCheckOrder(Long id) {
        // 1.1 校验存在，且草稿
        WmsCheckOrderDO order = validateCheckOrderPrepare(id);
        // 1.2 校验盘库单明细存在
        List<WmsCheckOrderDetailDO> details = checkOrderDetailService.validateCheckOrderDetailListExists(id);

        // 2. 完成盘库单
        checkOrderMapper.updateById(new WmsCheckOrderDO().setId(id)
                .setStatus(WmsOrderStatusEnum.FINISHED.getStatus()));

        // 3. 盘点库存
        changeInventory(order, details);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCheckOrder(Long id) {
        // 1. 校验存在，且草稿
        validateCheckOrderPrepare(id);

        // 2. 作废盘库单
        checkOrderMapper.updateById(new WmsCheckOrderDO().setId(id)
                .setStatus(WmsOrderStatusEnum.CANCELED.getStatus()));
    }

    @Override
    public WmsCheckOrderDO getCheckOrder(Long id) {
        return checkOrderMapper.selectById(id);
    }

    @Override
    public PageResult<WmsCheckOrderDO> getCheckOrderPage(WmsCheckOrderPageReqVO pageReqVO) {
        return checkOrderMapper.selectPage(pageReqVO);
    }

    @Override
    public long getCheckOrderCountByWarehouseId(Long warehouseId) {
        return checkOrderMapper.selectCountByWarehouseId(warehouseId);
    }

    private void validateCheckOrderSaveData(WmsCheckOrderSaveReqVO reqVO) {
        // 校验盘库单号唯一
        validateCheckOrderNoUnique(reqVO.getId(), reqVO.getNo());
        // 校验仓库存在
        warehouseService.validateWarehouseExists(reqVO.getWarehouseId());
    }

    private void validateCheckOrderNoUnique(Long id, String no) {
        WmsCheckOrderDO order = checkOrderMapper.selectByNo(no);
        if (order == null) {
            return;
        }
        if (id == null || ObjectUtil.notEqual(order.getId(), id)) {
            throw exception(CHECK_ORDER_NO_DUPLICATE);
        }
    }

    private WmsCheckOrderDO validateCheckOrderExists(Long id) {
        WmsCheckOrderDO order = id == null ? null : checkOrderMapper.selectById(id);
        if (order == null) {
            throw exception(CHECK_ORDER_NOT_EXISTS);
        }
        return order;
    }

    /**
     * 校验盘库单存在且为草稿状态
     *
     * @param id 盘库单编号
     * @return 盘库单
     */
    private WmsCheckOrderDO validateCheckOrderPrepare(Long id) {
        WmsCheckOrderDO order = validateCheckOrderExists(id);
        if (ObjectUtil.notEqual(order.getStatus(), WmsOrderStatusEnum.PREPARE.getStatus())) {
            throw exception(CHECK_ORDER_STATUS_NOT_PREPARE);
        }
        return order;
    }

    /**
     * 盘点盘库单对应库存
     *
     * @param order 盘库单
     * @param details 盘库单明细列表
     */
    private void changeInventory(WmsCheckOrderDO order, List<WmsCheckOrderDetailDO> details) {
        List<WmsInventoryChangeReqDTO.Item> items = new ArrayList<>(details.size());
        for (WmsCheckOrderDetailDO detail : details) {
            BigDecimal differenceQuantity = detail.getCheckQuantity().subtract(detail.getQuantity());
            if (differenceQuantity.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            WmsInventoryChangeReqDTO.Item item = BeanUtils.toBean(detail, WmsInventoryChangeReqDTO.Item.class)
                    .setQuantity(differenceQuantity);
            items.add(item);
        }
        if (CollUtil.isEmpty(items)) {
            return;
        }
        inventoryService.changeInventory(new WmsInventoryChangeReqDTO()
                .setOrderId(order.getId()).setOrderNo(order.getNo())
                .setOrderType(WmsInventoryOrderTypeEnum.CHECK.getType()).setItems(items));
    }

}
