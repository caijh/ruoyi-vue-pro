package cn.iocoder.yudao.module.system.service.sms;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.system.dal.dataobject.sms.SmsCodeDO;
import cn.iocoder.yudao.module.system.dal.mysql.sms.SmsCodeMapper;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import cn.iocoder.yudao.module.system.framework.sms.SmsCodeProperties;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Date;

import static cn.hutool.core.util.RandomUtil.randomInt;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 短信验证码 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SmsCodeServiceImpl implements SmsCodeService {

    @Resource
    private SmsCodeProperties smsCodeProperties;

    @Resource
    private SmsCodeMapper smsCodeMapper;

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private MemberUserApi memberUserApi;

    @Resource
    private SmsSendService smsSendService;

    @Override
    public void sendSmsCode(String mobile, Integer scene, String createIp) {
        SmsSceneEnum sceneEnum = SmsSceneEnum.getCodeByScene(scene);
        Assert.notNull(sceneEnum, "验证码场景({}) 查找不到配置", scene);
        // 创建验证码
        String code = createSmsCode(mobile, scene, createIp);
        // 发送验证码
        smsSendService.sendSingleSms(mobile, null, null,
                sceneEnum.getTemplateCode(), MapUtil.of("code", code));
    }

    private String createSmsCode(String mobile, Integer scene, String ip) {
        // 校验是否可以发送验证码，不用筛选场景
        SmsCodeDO lastSmsCode = smsCodeMapper.selectLastByMobile(mobile, null,null);
        if (lastSmsCode != null) {
            if (lastSmsCode.getTodayIndex() >= smsCodeProperties.getSendMaximumQuantityPerDay()) { // 超过当天发送的上限。
                throw ServiceExceptionUtil.exception(SMS_CODE_EXCEED_SEND_MAXIMUM_QUANTITY_PER_DAY);
            }
            if (System.currentTimeMillis() - lastSmsCode.getCreateTime().getTime()
                    < smsCodeProperties.getSendFrequency().toMillis()) { // 发送过于频繁
                throw ServiceExceptionUtil.exception(SMS_CODE_SEND_TOO_FAST);
            }
            // TODO 芋艿：提升，每个 IP 每天可发送数量
            // TODO 芋艿：提升，每个 IP 每小时可发送数量
        }

        // 创建验证码记录
        String code = String.valueOf(randomInt(smsCodeProperties.getBeginCode(), smsCodeProperties.getEndCode() + 1));
        SmsCodeDO newSmsCode = SmsCodeDO.builder().mobile(mobile).code(code)
                .scene(scene).todayIndex(lastSmsCode != null ? lastSmsCode.getTodayIndex() + 1 : 1)
                .createIp(ip).used(false).build();
        smsCodeMapper.insert(newSmsCode);
        return code;
    }

    @Override
    public void useSmsCode(String mobile, Integer scene, String code, String usedIp) {
        // 检测验证码是否有效
        SmsCodeDO lastSmsCode = this.checkSmsCode0(mobile, code, scene);
        // 使用验证码
        smsCodeMapper.updateById(SmsCodeDO.builder().id(lastSmsCode.getId())
                .used(true).usedTime(new Date()).usedIp(usedIp).build());
    }

    @Override
    public void checkSmsCode(String mobile, String code, Integer scene) {
        checkSmsCode0(mobile, code, scene);
    }

    public SmsCodeDO checkSmsCode0(String mobile, String code, Integer scene) {
        // 校验验证码
        SmsCodeDO lastSmsCode = smsCodeMapper.selectLastByMobile(mobile,code,scene);
        // 若验证码不存在，抛出异常
        if (lastSmsCode == null) {
            throw ServiceExceptionUtil.exception(SMS_CODE_NOT_FOUND);
        }
        // 超过时间
        if (System.currentTimeMillis() - lastSmsCode.getCreateTime().getTime()
                >= smsCodeProperties.getExpireTimes().toMillis()) { // 验证码已过期
            throw ServiceExceptionUtil.exception(SMS_CODE_EXPIRED);
        }
        // 判断验证码是否已被使用
        if (Boolean.TRUE.equals(lastSmsCode.getUsed())) {
            throw ServiceExceptionUtil.exception(SMS_CODE_USED);
        }
        return lastSmsCode;
    }

}
