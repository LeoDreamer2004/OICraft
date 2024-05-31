package org.dindier.oicraft.assets.config;

import org.dindier.oicraft.controller.interceptor.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private UserInfoInterceptor userInfoInterceptor;

    @Autowired
    public void setUserInfoInterceptor(UserInfoInterceptor userInfoInterceptor) {
        this.userInfoInterceptor = userInfoInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(userInfoInterceptor);
    }
}
