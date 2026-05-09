package cn.iocoder.yudao.module.wms.service.warehouse;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.wms.controller.admin.warehouse.vo.area.WmsWarehouseAreaPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.warehouse.vo.area.WmsWarehouseAreaSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.warehouse.WmsWarehouseAreaDO;
import cn.iocoder.yudao.module.wms.dal.mysql.warehouse.WmsWarehouseAreaMapper;
import cn.iocoder.yudao.module.wms.framework.config.WmsProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.*;

/**
 * WMS 库区 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsWarehouseAreaServiceImpl implements WmsWarehouseAreaService {

    @Resource
    private WmsWarehouseAreaMapper warehouseAreaMapper;
    @Resource
    private WmsWarehouseService warehouseService;
    @Resource
    private WmsProperties wmsProperties;

    @Override
    public Long createWarehouseArea(WmsWarehouseAreaSaveReqVO createReqVO) {
        validateAreaEnabled();
        // 校验数据
        validateWarehouseAreaSaveData(createReqVO);

        // 插入
        WmsWarehouseAreaDO area = BeanUtils.toBean(createReqVO, WmsWarehouseAreaDO.class);
        warehouseAreaMapper.insert(area);
        // 返回
        return area.getId();
    }

    @Override
    public void updateWarehouseArea(WmsWarehouseAreaSaveReqVO updateReqVO) {
        validateAreaEnabled();
        // 校验存在
        validateWarehouseAreaExists(updateReqVO.getId());
        // 校验数据
        validateWarehouseAreaSaveData(updateReqVO);

        // 更新
        WmsWarehouseAreaDO updateObj = BeanUtils.toBean(updateReqVO, WmsWarehouseAreaDO.class);
        warehouseAreaMapper.updateById(updateObj);
    }

    private void validateWarehouseAreaSaveData(WmsWarehouseAreaSaveReqVO reqVO) {
        warehouseService.validateWarehouseExists(reqVO.getWarehouseId());
        validateWarehouseAreaNameUnique(reqVO.getId(), reqVO.getWarehouseId(), reqVO.getName());
        validateWarehouseAreaCodeUnique(reqVO.getId(), reqVO.getCode());
    }

    private void validateWarehouseAreaNameUnique(Long id, Long warehouseId, String name) {
        WmsWarehouseAreaDO area = warehouseAreaMapper.selectByName(warehouseId, name);
        if (area == null) {
            return;
        }
        if (ObjUtil.notEqual(area.getId(), id)) {
            throw exception(WAREHOUSE_AREA_NAME_DUPLICATE);
        }
    }

    private void validateWarehouseAreaCodeUnique(Long id, String code) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        WmsWarehouseAreaDO area = warehouseAreaMapper.selectByCode(code);
        if (area == null) {
            return;
        }
        if (ObjUtil.notEqual(area.getId(), id)) {
            throw exception(WAREHOUSE_AREA_CODE_DUPLICATE);
        }
    }

    @Override
    public void deleteWarehouseArea(Long id) {
        validateAreaEnabled();
        // 校验存在
        validateWarehouseAreaExists(id);

        // 删除
        warehouseAreaMapper.deleteById(id);
    }

    @Override
    public WmsWarehouseAreaDO validateWarehouseAreaExists(Long id) {
        validateAreaEnabled();
        WmsWarehouseAreaDO area = warehouseAreaMapper.selectById(id);
        if (area == null) {
            throw exception(WAREHOUSE_AREA_NOT_EXISTS);
        }
        return area;
    }

    @Override
    public WmsWarehouseAreaDO getWarehouseArea(Long id) {
        validateAreaEnabled();
        return warehouseAreaMapper.selectById(id);
    }

    @Override
    public PageResult<WmsWarehouseAreaDO> getWarehouseAreaPage(WmsWarehouseAreaPageReqVO pageReqVO) {
        validateAreaEnabled();
        return warehouseAreaMapper.selectPage(pageReqVO);
    }

    @Override
    public List<WmsWarehouseAreaDO> getWarehouseAreaList(Long warehouseId) {
        validateAreaEnabled();
        return warehouseAreaMapper.selectListByWarehouseId(warehouseId);
    }

    private void validateAreaEnabled() {
        if (!wmsProperties.isAreaEnabled()) {
            throw exception(WMS_AREA_DISABLED);
        }
    }

}
