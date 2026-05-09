package cn.iocoder.yudao.module.wms.framework.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * WMS 模块配置
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WmsProperties.class)
public class WmsConfiguration {
}
