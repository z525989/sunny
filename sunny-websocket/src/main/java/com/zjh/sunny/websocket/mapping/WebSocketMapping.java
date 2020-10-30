package com.zjh.sunny.websocket.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebSocketMapping {

    /**
     * 是否校验token
     */
    boolean isAuth() default false;

    /**
     * 是否支持异步
     */
    boolean isAsync() default true;

    /**
     * 请求协议
     */
    String protocol();
}

