package com.springboot.ai.aiservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.function.Function;

@Slf4j
@Configuration
public class AssistantTools {

    public record Request(String name) {

    }

    public record Response(String time) {

    }

    @Bean
    @Description("获取当前时间")
    public FunctionCallback currentTime() {
        return FunctionCallbackWrapper.builder(new Function<Request, Response>() {
                    @Override
                    public Response apply(Request request) {
                        log.info("request:{}", request);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = dateFormat.format(System.currentTimeMillis());
                        String answer = "当前时间为：" + time;
                        return new Response(answer);
                    }
                })
                .withDescription("获取当前时间")
                .withInputType(Request.class)
                .withName("currentTime")
                .build();
    }
}
