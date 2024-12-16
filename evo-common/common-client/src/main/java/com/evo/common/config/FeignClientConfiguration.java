package com.evo.common.config;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
public class FeignClientConfiguration {

    @Bean
    public FeignClientInterceptor requestInterceptor() {
        FeignClientInterceptor feignClientInterceptor = new FeignClientInterceptor();
        return feignClientInterceptor;
    }


}
