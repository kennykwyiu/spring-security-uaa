package com.kenny.uaa.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.kenny.uaa.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// kenny.sms-provider.name=ali
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kenny.sms-provider", name = "name", havingValue = "ali")
public class SmsServiceAliSmsImpl implements SmsService{

    private final IAcsClient client;
    private final AppProperties appProperties;

    @Override
    public void send(String mobile, String msg) {
        CommonRequest request = getRequest(mobile, msg);
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("SMS sending result {}", response.getData());
        } catch (ServerException e) {
            log.error("Server exception occurred while sending SMS: {}", e.getLocalizedMessage());
        } catch (ClientException e) {
            log.error("Client exception occurred while sending SMS: {}", e.getLocalizedMessage());
        }
    }

    private CommonRequest getRequest(String mobile, String msg) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(appProperties.getSmsProvider().getApiUrl());
        request.setSysAction("SendSms");
        request.setSysVersion("2017-05-25");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", "Login Verification");
        request.putQueryParameter("TemplateCode", "SMS_1610048");
        request.putQueryParameter("TemplateParam", "{\"code\":\"" +
                msg +
                "\",\"product\":\"kenny.com Practical Spring Security\"}");
        return request;
    }
}
