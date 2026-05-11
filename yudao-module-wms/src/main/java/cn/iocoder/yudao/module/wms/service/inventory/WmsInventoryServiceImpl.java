package cn.iocoder.yudao.module.wms.service.inventory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.WmsInventoryPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDO;
import cn.iocoder.yudao.module.wms.dal.mysql.inventory.WmsInventoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * WMS 库存 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsInventoryServiceImpl implements WmsInventoryService {

    @Resource
    private WmsInventoryMapper inventoryMapper;

    @Override
    public PageResult<WmsInventoryDO> getInventoryPage(WmsInventoryPageReqVO pageReqVO) {
        return inventoryMapper.selectPage(pageReqVO);
    }

    @Override
    public long getInventoryCountBySkuId(Long skuId) {
        return inventoryMapper.selectCountBySkuId(skuId);
    }

}
