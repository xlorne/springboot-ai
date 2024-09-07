package com.springboot.ai.advisors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.AdvisedRequest;
import org.springframework.ai.chat.client.RequestResponseAdvisor;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.Map;

@Slf4j
public class LoggingAdvisor implements RequestResponseAdvisor {

    @Override
    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
        log.info("request:{},context:{}", request, context);
        return request;
    }

    @Override
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> context) {
        log.info("response:{},context:{}", response, context);
        return response;
    }
}
