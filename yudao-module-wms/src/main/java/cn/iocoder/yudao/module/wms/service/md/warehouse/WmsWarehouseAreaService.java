package cn.iocoder.yudao.module.wms.service.md.warehouse;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.wms.controller.admin.md.warehouse.vo.area.WmsWarehouseAreaPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.md.warehouse.vo.area.WmsWarehouseAreaSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.warehouse.WmsWarehouseAreaDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * WMS 库区 Service 接口
 *
 * @author 芋道源码
 */
public interface WmsWarehouseAreaService {

    /**
     * 创建库区
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWarehouseArea(@Valid WmsWarehouseAreaSaveReqVO createReqVO);

    /**
     * 更新库区
     *
     * @param updateReqVO 更新信息
     */
    void updateWarehouseArea(@Valid WmsWarehouseAreaSaveReqVO updateReqVO);

    /**
     * 删除库区
     *
     * @param id 编号
     */
    void deleteWarehouseArea(Long id);

    /**
     * 校验库区存在
     *
     * @param id 编号
     * @return 库区
     */
    WmsWarehouseAreaDO validateWarehouseAreaExists(Long id);

    /**
     * 校验并标准化库区编号
     *
     * @param id 库区编号
     * @param warehouseId 仓库编号
     * @return 库区编号；未启用库区模式或为空时，返回 {@link WmsWarehouseAreaDO#ID_EMPTY}
     */
    Long validateAndNormalizeWarehouseAreaId(Long id, Long warehouseId);

    /**
     * 获得库区
     *
     * @param id 编号
     * @return 库区
     */
    WmsWarehouseAreaDO getWarehouseArea(Long id);

    /**
     * 获得库区分页
     *
     * @param pageReqVO 分页查询
     * @return 库区分页
     */
    PageResult<WmsWarehouseAreaDO> getWarehouseAreaPage(WmsWarehouseAreaPageReqVO pageReqVO);

    /**
     * 获得库区列表
     *
     * @param warehouseId 仓库编号
     * @return 库区列表
     */
    List<WmsWarehouseAreaDO> getWarehouseAreaList(Long warehouseId);

    /**
     * 按编号集合获得库区列表
     *
     * @param ids 编号集合
     * @return 库区列表
     */
    List<WmsWarehouseAreaDO> getWarehouseAreaList(Collection<Long> ids);

    /**
     * 按编号集合获得库区 Map
     *
     * @param ids 编号集合
     * @return 库区 Map
     */
    default Map<Long, WmsWarehouseAreaDO> getWarehouseAreaMap(Collection<Long> ids) {
        return convertMap(getWarehouseAreaList(ids), WmsWarehouseAreaDO::getId);
    }

}
