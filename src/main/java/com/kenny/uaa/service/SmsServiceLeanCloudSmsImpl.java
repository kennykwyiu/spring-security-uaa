package com.kenny.uaa.service;

import cn.leancloud.sms.AVSMS;
import cn.leancloud.sms.AVSMSOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
@ConditionalOnProperty(prefix = "kenny.sms-provider", name = "name", havingValue = "lean-cloud")
public class SmsServiceLeanCloudSmsImpl implements SmsService{
    @Override
    public void send(String mobile, String msg) {
        AVSMSOption option = new AVSMSOption();
        option.setTtl(10);
        option.setApplicationName("kenny.com Practical Spring Security");
        option.setOperation("Two-Step Verification");
        option.setTemplateName("Login Verification");
        option.setSignatureName("kenny.com");
        option.setType(AVSMS.TYPE.TEXT_SMS);
        option.setEnvMap(Map.of("smsCode", msg));
        AVSMS.requestSMSCodeInBackground(mobile, option)
                .take(1)
                .subscribe(
                        (res) -> log.info("SMS sent successfully: {}", res),
                        (err) -> log.error("Server exception occurred while sending SMS: {}", err.getLocalizedMessage())
                );
    }
}
