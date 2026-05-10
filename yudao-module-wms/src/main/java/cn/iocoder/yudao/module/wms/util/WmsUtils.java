package cn.iocoder.yudao.module.wms.util;

import cn.hutool.core.util.RandomUtil;
import lombok.experimental.UtilityClass;

/**
 * WMS 工具类
 *
 * @author 芋道源码
 */
@UtilityClass
public class WmsUtils {

    private static final int BAR_CODE_LENGTH = 8;

    /**
     * 生成条码
     *
     * @return 条码
     */
    public String generateBarCode() {
        return RandomUtil.randomNumbers(BAR_CODE_LENGTH);
    }

}
