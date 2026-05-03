package cn.iocoder.yudao.module.iot.service.device.message;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.mq.producer.IotDeviceMessageProducer;
import cn.iocoder.yudao.module.iot.dal.dataobject.device.IotDeviceMessageDO;
import cn.iocoder.yudao.module.iot.dal.tdengine.IotDeviceMessageMapper;
import cn.iocoder.yudao.module.iot.service.device.IotDeviceService;
import cn.iocoder.yudao.module.iot.service.device.property.IotDevicePropertyService;
import cn.iocoder.yudao.module.iot.service.ota.IotOtaTaskRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link IotDeviceMessageServiceImpl} 的单元测试
 *
 * @author 芋道源码
 */
public class IotDeviceMessageServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private IotDeviceMessageServiceImpl service;

    @Mock
    private IotDeviceService deviceService;
    @Mock
    private IotDevicePropertyService devicePropertyService;
    @Mock
    private IotOtaTaskRecordService otaTaskRecordService;
    @Mock
    private IotDeviceMessageMapper deviceMessageMapper;
    @Mock
    private IotDeviceMessageProducer deviceMessageProducer;

    @Test
    public void testCreateDeviceLogAsync_tsFallback_whenNull() {
        // 准备：构造一条 ts 为 null 的消息
        IotDeviceMessage message = buildMessage();
        long before = System.currentTimeMillis();

        // 调用
        service.createDeviceLogAsync(message);
        long after = System.currentTimeMillis();

        // 断言：mapper.insert 接收到的 messageDO 已经被填上 ts，值在 [before, after] 区间
        ArgumentCaptor<IotDeviceMessageDO> captor = ArgumentCaptor.forClass(IotDeviceMessageDO.class);
        verify(deviceMessageMapper).insert(captor.capture());
        Long actualTs = captor.getValue().getTs();
        assertNotNull(actualTs, "ts 不应为空");
        assertTrue(actualTs >= before && actualTs <= after,
                "ts 应在调用前后区间内； 实际 = " + actualTs);
    }

    @Test
    public void testCreateDeviceLogAsync_swallowMapperException() {
        // 准备：mapper.insert 抛异常，验证 @Async 方法内部 try/catch 兜底，不向上抛
        IotDeviceMessage message = buildMessage();
        doThrow(new RuntimeException("DB unavailable")).when(deviceMessageMapper).insert(any());

        // 调用 & 断言
        assertDoesNotThrow(() -> service.createDeviceLogAsync(message));
        verify(deviceMessageMapper).insert(any(IotDeviceMessageDO.class));
    }

    /** 构造一条最简属性上报消息 */
    private IotDeviceMessage buildMessage() {
        IotDeviceMessage message = new IotDeviceMessage();
        message.setId("msg-1");
        message.setDeviceId(2L);
        message.setMethod(IotDeviceMessageMethodEnum.PROPERTY_POST.getMethod());
        message.setParams(new HashMap<>());
        return message;
    }

}
