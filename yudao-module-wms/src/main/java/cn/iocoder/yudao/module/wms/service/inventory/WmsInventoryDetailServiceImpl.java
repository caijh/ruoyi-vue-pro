package cn.iocoder.yudao.module.wms.service.inventory;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.detail.WmsInventoryDetailPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDetailDO;
import cn.iocoder.yudao.module.wms.dal.mysql.inventory.WmsInventoryDetailMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

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

}
