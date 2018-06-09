package com.lchli.lottery;

import com.lchli.lottery.interceptor.MiNoteInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MiNoteAppConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new MiNoteInterceptor()).addPathPatterns("/api/sec/**");

    }
}
