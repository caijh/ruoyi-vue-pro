package cn.iocoder.yudao.module.wms.service.inventory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.WmsInventoryPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDO;
import cn.iocoder.yudao.module.wms.service.inventory.dto.WmsInventoryChangeReqDTO;

/**
 * WMS 库存 Service 接口
 *
 * @author 芋道源码
 */
public interface WmsInventoryService {

    /**
     * 获得库存统计分页
     *
     * @param pageReqVO 分页查询
     * @return 库存统计分页
     */
    PageResult<WmsInventoryDO> getInventoryPage(WmsInventoryPageReqVO pageReqVO);

    /**
     * 获得指定 SKU 的库存数量
     *
     * @param skuId SKU 编号
     * @return 库存数量
     */
    long getInventoryCountBySkuId(Long skuId);

    /**
     * 变更库存
     *
     * @param reqDTO 库存变更请求
     */
    void changeInventory(WmsInventoryChangeReqDTO reqDTO);

}
