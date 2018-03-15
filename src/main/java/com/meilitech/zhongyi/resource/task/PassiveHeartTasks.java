package com.meilitech.zhongyi.resource.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilitech.zhongyi.resource.api.entity.PassiveHeart;
import com.meilitech.zhongyi.resource.api.enums.ResultType;
import com.meilitech.zhongyi.resource.api.request.PassiveHeartRequest;
import com.meilitech.zhongyi.resource.api.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

/**
 * @author Sendy
 */
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Component
public class PassiveHeartTasks {
    private static final Logger log = LoggerFactory.getLogger(PassiveHeartTasks.class);

    @Autowired
    private RestOperations restTemplate;

    @Value("${resource.heart.url}")
    private String heartUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${resource.heart.providercode}")
    private String providerCodes;

    /**
     * 被动心跳
     *
     * 定时发送服务器运行状态
     */
    @Scheduled(fixedDelay = 500000)
    public void  heart() {
        //todo
        PassiveHeartRequest passiveHeartRequest = new PassiveHeartRequest();
        PassiveHeart passiveHeart = new PassiveHeart();
        passiveHeart.setOperationStatus("1");
        passiveHeart.setProviderCode(providerCodes);
        passiveHeartRequest.setData(passiveHeart);
        BaseResponse response =new BaseResponse();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON);
            List<Charset> list =new ArrayList<>(  );
            list.add(Charset.forName( "UTF-8" ) );
            headers.setAcceptCharset( list );
            HttpEntity<Object> formEntity = new HttpEntity<>( passiveHeartRequest, headers );
            log.info("心跳请求数据：{}", objectMapper.writeValueAsString(passiveHeartRequest));
            String msg = restTemplate.postForObject(heartUrl,formEntity, String.class);
            response.setErrmsg( new String(msg.getBytes("iso-8859-1"),"utf-8") );
            response.setErrcode( ResultType.SUCCESS.getCode().toString() );
            log.info("心跳返回数据：{}", objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            log.info("心跳请求返回结果异常：{}", e);
        }
    }
}
