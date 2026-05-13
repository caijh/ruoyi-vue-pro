package cn.iocoder.yudao.module.wms.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

/**
 * WMS 配置项
 *
 * @author 芋道源码
 */
@ConfigurationProperties(prefix = "yudao.wms")
@Validated
@Data
public class WmsProperties {

    /**
     * 是否启用库区模式
     */
    @NotNull(message = "是否启用库区模式不能为空")
    private Boolean areaEnable = true;

    public boolean isAreaEnabled() {
        return Boolean.TRUE.equals(areaEnable);
    }

}
