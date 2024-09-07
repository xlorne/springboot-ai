package com.springboot.ai.aiservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.function.Function;

@Slf4j
@Component
public class AssistantTools {

    public record Request(String name){

    }

    public record Response(String time){

    }

    @Bean
    @Description("获取当前时间")
    public Function<Request,Response> currentTime() {
        return request -> {
            log.info("request:{}",request);
            return new Response(LocalTime.now().toString());
        };
    }
}
