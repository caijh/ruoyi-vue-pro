package cn.iocoder.yudao.module.iot.service.rule.scene.matcher.trigger;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.module.iot.core.enums.IotDeviceMessageMethodEnum;
import cn.iocoder.yudao.module.iot.core.mq.message.IotDeviceMessage;
import cn.iocoder.yudao.module.iot.core.topic.event.IotDeviceEventPostReqDTO;
import cn.iocoder.yudao.module.iot.dal.dataobject.rule.IotSceneRuleDO;
import cn.iocoder.yudao.module.iot.enums.rule.IotSceneRuleConditionOperatorEnum;
import cn.iocoder.yudao.module.iot.enums.rule.IotSceneRuleTriggerTypeEnum;
import cn.iocoder.yudao.module.iot.service.rule.scene.matcher.IotBaseConditionMatcherTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.util.RandomUtil.randomInt;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomLongId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link IotDeviceEventPostTriggerMatcher} 的单元测试
 *
 * @author HUIHUI
 */
public class IotDeviceEventPostTriggerMatcherTest extends IotBaseConditionMatcherTest {

    private IotDeviceEventPostTriggerMatcher matcher;

    @BeforeEach
    public void setUp() {
        matcher = new IotDeviceEventPostTriggerMatcher();
    }

    @Test
    public void testGetSupportedTriggerType_success() {
        // 准备参数
        // 无需准备参数

        // 调用
        IotSceneRuleTriggerTypeEnum result = matcher.getSupportedTriggerType();

        // 断言
        assertEquals(IotSceneRuleTriggerTypeEnum.DEVICE_EVENT_POST, result);
    }

    @Test
    public void testGetPriority_success() {
        // 准备参数
        // 无需准备参数

        // 调用
        int result = matcher.getPriority();

        // 断言
        assertEquals(30, result);
    }

    @Test
    public void testIsEnabled_success() {
        // 准备参数
        // 无需准备参数

        // 调用
        boolean result = matcher.isEnabled();

        // 断言
        assertTrue(result);
    }

    @Test
    public void testMatches_alarmEventSuccess() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("level", randomString())
                        .put("message", randomString())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertTrue(result);
    }

    @Test
    public void testMatches_errorEventSuccess() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("code", randomInt())
                        .put("description", randomString())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertTrue(result);
    }

    @Test
    public void testMatches_infoEventSuccess() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("status", randomString())
                        .put("timestamp", System.currentTimeMillis())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertTrue(result);
    }

    @Test
    public void testMatches_eventIdentifierMismatch() {
        // 准备参数
        String messageIdentifier = randomString();
        String triggerIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", messageIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("level", randomString())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(triggerIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertFalse(result);
    }

    @Test
    public void testMatches_wrongMessageMethod() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("level", randomString())
                        .build())
                .build();
        IotDeviceMessage message = new IotDeviceMessage();
        message.setDeviceId(randomLongId());
        message.setMethod(IotDeviceMessageMethodEnum.PROPERTY_POST.getMethod()); // 错误的方法
        message.setParams(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertFalse(result);
    }

    @Test
    public void testMatches_nullTriggerIdentifier() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("level", randomString())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = new IotSceneRuleDO.Trigger();
        trigger.setType(IotSceneRuleTriggerTypeEnum.DEVICE_EVENT_POST.getType());
        trigger.setIdentifier(null); // 缺少标识符

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertFalse(result);
    }

    @Test
    public void testMatches_nullMessageParams() {
        // 准备参数
        String eventIdentifier = randomString();
        IotDeviceMessage message = new IotDeviceMessage();
        message.setDeviceId(randomLongId());
        message.setMethod(IotDeviceMessageMethodEnum.EVENT_POST.getMethod());
        message.setParams(null);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertFalse(result);
    }

    @Test
    public void testMatches_invalidMessageParams() {
        // 准备参数
        String eventIdentifier = randomString();
        IotDeviceMessage message = new IotDeviceMessage();
        message.setDeviceId(randomLongId());
        message.setMethod(IotDeviceMessageMethodEnum.EVENT_POST.getMethod());
        message.setParams(randomString()); // 不是 Map 类型
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertFalse(result);
    }

    @Test
    public void testMatches_missingEventIdentifierInParams() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("level", randomString())
                        .build()) // 缺少 identifier 字段
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertFalse(result);
    }

    @Test
    public void testMatches_nullTrigger() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("level", randomString())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);

        // 调用
        boolean result = matcher.matches(message, null);

        // 断言
        assertFalse(result);
    }

    @Test
    public void testMatches_nullTriggerType() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("level", randomString())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = new IotSceneRuleDO.Trigger();
        trigger.setType(null);
        trigger.setIdentifier(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertFalse(result);
    }

    @Test
    public void testMatches_complexEventValueSuccess() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("type", randomString())
                        .put("duration", randomInt())
                        .put("components", new String[]{randomString(), randomString()})
                        .put("priority", randomString())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertTrue(result);
    }

    @Test
    public void testMatches_emptyEventValueSuccess() {
        // 准备参数
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.ofEntries()) // 空的事件值
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        assertTrue(result);
    }

    @Test
    public void testMatches_caseSensitiveIdentifierMismatch() {
        // 准备参数
        String eventIdentifier = randomString().toUpperCase(); // 大写
        String triggerIdentifier = eventIdentifier.toLowerCase(); // 小写
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", MapUtil.builder(new HashMap<String, Object>())
                        .put("level", randomString())
                        .build())
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(triggerIdentifier);

        // 调用
        boolean result = matcher.matches(message, trigger);

        // 断言
        // 根据实际实现，这里可能需要调整期望结果
        // 如果实现是大小写敏感的，则应该为 false
        assertFalse(result);
    }

    @Test
    public void testMatches_scalarValueEqualsSuccess() {
        // 标量事件值 + operator='=' + value 命中：必须匹配；防止"把整个 params Map 当作源值"的回归
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", "normal")
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.EQUALS.getOperator());
        trigger.setValue("normal");

        boolean result = matcher.matches(message, trigger);

        assertTrue(result);
    }

    @Test
    public void testMatches_scalarValueEqualsMismatch() {
        // 标量事件值不等于配置值：应不匹配
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", "abnormal")
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.EQUALS.getOperator());
        trigger.setValue("normal");

        boolean result = matcher.matches(message, trigger);

        assertFalse(result);
    }

    @Test
    public void testMatches_pojoParamsScalarValueSuccess() {
        // 本地总线场景：params 是 IotDeviceEventPostReqDTO POJO 而非 Map，仍应能正确匹配
        String eventIdentifier = randomString();
        IotDeviceMessage message = new IotDeviceMessage();
        message.setDeviceId(randomLongId());
        message.setMethod(IotDeviceMessageMethodEnum.EVENT_POST.getMethod());
        message.setParams(IotDeviceEventPostReqDTO.of(eventIdentifier, "normal"));
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.EQUALS.getOperator());
        trigger.setValue("normal");

        boolean result = matcher.matches(message, trigger);

        assertTrue(result);
    }

    @Test
    public void testMatches_structValueEqualsSuccess() {
        // 结构体事件值 + 比较值是 JSON 对象字面量：JSON 反序列化后整体相等
        String eventIdentifier = randomString();
        Map<String, Object> eventValue = MapUtil.builder(new HashMap<String, Object>())
                .put("level", "high")
                .put("message", "over temperature")
                .build();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", eventValue)
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.EQUALS.getOperator());
        trigger.setValue("{\"level\":\"high\",\"message\":\"over temperature\"}");

        boolean result = matcher.matches(message, trigger);

        assertTrue(result);
    }

    @Test
    public void testMatches_structValueEqualsKeyOrderInsensitive() {
        // 比较值的 JSON 字段顺序与事件 value 不同，应仍然匹配（HashMap.equals 与顺序无关）
        String eventIdentifier = randomString();
        Map<String, Object> eventValue = MapUtil.builder(new HashMap<String, Object>())
                .put("level", "high")
                .put("code", 500)
                .build();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", eventValue)
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.EQUALS.getOperator());
        trigger.setValue("{\"code\":500,\"level\":\"high\"}");

        boolean result = matcher.matches(message, trigger);

        assertTrue(result);
    }

    @Test
    public void testMatches_structValueEqualsMismatch() {
        // 结构体值字段不一致：应不匹配
        String eventIdentifier = randomString();
        Map<String, Object> eventValue = MapUtil.builder(new HashMap<String, Object>())
                .put("level", "low")
                .build();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", eventValue)
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.EQUALS.getOperator());
        trigger.setValue("{\"level\":\"high\"}");

        boolean result = matcher.matches(message, trigger);

        assertFalse(result);
    }

    @Test
    public void testMatches_structValueNotEqualsSuccess() {
        // 结构体值 + != 操作符：内容不一致时应匹配
        String eventIdentifier = randomString();
        Map<String, Object> eventValue = MapUtil.builder(new HashMap<String, Object>())
                .put("level", "low")
                .build();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", eventValue)
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.NOT_EQUALS.getOperator());
        trigger.setValue("{\"level\":\"high\"}");

        boolean result = matcher.matches(message, trigger);

        assertTrue(result);
    }

    @Test
    public void testMatches_arrayValueEqualsSuccess() {
        // 数组事件值：JSON 解析后按序相等
        String eventIdentifier = randomString();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", ListUtil.of("a", "b", "c"))
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.EQUALS.getOperator());
        trigger.setValue("[\"a\",\"b\",\"c\"]");

        boolean result = matcher.matches(message, trigger);

        assertTrue(result);
    }

    @Test
    public void testMatches_structValueInvalidJsonComparator() {
        // 比较值不是合法 JSON：结构体场景下视为不匹配，不抛异常
        String eventIdentifier = randomString();
        Map<String, Object> eventValue = MapUtil.builder(new HashMap<String, Object>())
                .put("level", "high")
                .build();
        Map<String, Object> eventParams = MapUtil.builder(new HashMap<String, Object>())
                .put("identifier", eventIdentifier)
                .put("value", eventValue)
                .build();
        IotDeviceMessage message = createEventPostMessage(eventParams);
        IotSceneRuleDO.Trigger trigger = createValidTrigger(eventIdentifier);
        trigger.setOperator(IotSceneRuleConditionOperatorEnum.EQUALS.getOperator());
        trigger.setValue("not a json");

        boolean result = matcher.matches(message, trigger);

        assertFalse(result);
    }

    // ========== 辅助方法 ==========

    /**
     * 创建事件上报消息
     */
    private IotDeviceMessage createEventPostMessage(Map<String, Object> eventParams) {
        IotDeviceMessage message = new IotDeviceMessage();
        message.setDeviceId(randomLongId());
        message.setMethod(IotDeviceMessageMethodEnum.EVENT_POST.getMethod());
        message.setParams(eventParams);
        return message;
    }

    /**
     * 创建有效的触发器
     */
    private IotSceneRuleDO.Trigger createValidTrigger(String identifier) {
        IotSceneRuleDO.Trigger trigger = new IotSceneRuleDO.Trigger();
        trigger.setType(IotSceneRuleTriggerTypeEnum.DEVICE_EVENT_POST.getType());
        trigger.setIdentifier(identifier);
        return trigger;
    }

}
