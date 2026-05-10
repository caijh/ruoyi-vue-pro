package cn.iocoder.yudao.module.wms.service.md.item;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.wms.controller.admin.md.item.vo.brand.WmsItemBrandPageReqVO;
import cn.iocoder.yudao.module.wms.controller.admin.md.item.vo.brand.WmsItemBrandSaveReqVO;
import cn.iocoder.yudao.module.wms.dal.dataobject.md.item.WmsItemBrandDO;
import cn.iocoder.yudao.module.wms.dal.mysql.md.item.WmsItemBrandMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.wms.enums.ErrorCodeConstants.ITEM_BRAND_NOT_EXISTS;

/**
 * WMS 商品品牌 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class WmsItemBrandServiceImpl implements WmsItemBrandService {

    @Resource
    private WmsItemBrandMapper brandMapper;

    @Override
    public Long createItemBrand(WmsItemBrandSaveReqVO createReqVO) {
        WmsItemBrandDO brand = BeanUtils.toBean(createReqVO, WmsItemBrandDO.class);
        brandMapper.insert(brand);
        return brand.getId();
    }

    @Override
    public void updateItemBrand(WmsItemBrandSaveReqVO updateReqVO) {
        // 校验存在
        validateItemBrandExists(updateReqVO.getId());

        // 更新
        WmsItemBrandDO updateObj = BeanUtils.toBean(updateReqVO, WmsItemBrandDO.class);
        brandMapper.updateById(updateObj);
    }

    @Override
    public void deleteItemBrand(Long id) {
        // 校验存在
        validateItemBrandExists(id);

        // 删除
        brandMapper.deleteById(id);
    }

    @Override
    public WmsItemBrandDO validateItemBrandExists(Long id) {
        WmsItemBrandDO brand = brandMapper.selectById(id);
        if (brand == null) {
            throw exception(ITEM_BRAND_NOT_EXISTS);
        }
        return brand;
    }

    @Override
    public WmsItemBrandDO getItemBrand(Long id) {
        return brandMapper.selectById(id);
    }

    @Override
    public PageResult<WmsItemBrandDO> getItemBrandPage(WmsItemBrandPageReqVO pageReqVO) {
        return brandMapper.selectPage(pageReqVO);
    }

    @Override
    public List<WmsItemBrandDO> getItemBrandList() {
        return brandMapper.selectList();
    }

}
