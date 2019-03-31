package com.fycx.eventsdemo;

import com.fycx.event.interceptor.EventInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class UserInfoNullInterceptor implements EventInterceptor {
    @Override
    public boolean interceptRule() {
        return true;
    }


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Callback{

    }
}
