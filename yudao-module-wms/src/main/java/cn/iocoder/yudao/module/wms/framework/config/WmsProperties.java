package cn.iocoder.yudao.module.wms.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.AssertTrue;
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

    /**
     * 是否启用批次/效期/库存明细模式
     *
     * 依赖 {@link #areaEnable} 为 true
     */
    @NotNull(message = "是否启用批次/效期/库存明细模式不能为空")
    private Boolean batchEnable = true;

    public boolean isAreaEnabled() {
        return Boolean.TRUE.equals(areaEnable);
    }

    public boolean isBatchEnabled() {
        return Boolean.TRUE.equals(batchEnable);
    }

    @AssertTrue(message = "WMS 批次/效期模式依赖库区模式，请先开启 yudao.wms.area-enable")
    public boolean isBatchDependsOnAreaValid() {
        return !isBatchEnabled() || isAreaEnabled();
    }

}
