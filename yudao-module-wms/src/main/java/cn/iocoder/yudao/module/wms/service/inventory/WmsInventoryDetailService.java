package cn.iocoder.yudao.module.wms.service.inventory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.wms.controller.admin.inventory.vo.detail.WmsInventoryDetailPageReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.inventory.WmsInventoryDetailDO;

/**
 * WMS 库存明细 Service 接口
 *
 * @author 芋道源码
 */
public interface WmsInventoryDetailService {

    /**
     * 获得库存明细分页
     *
     * @param pageReqVO 分页查询
     * @return 库存明细分页
     */
    PageResult<WmsInventoryDetailDO> getInventoryDetailPage(WmsInventoryDetailPageReqVO pageReqVO);

}
