package com.fycx.event.annotation;

import android.view.View;

import com.fycx.event.interceptor.EventInterceptor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ViewEvent(
        listenerSetMethod = "setOnLongClickListener",
        listenerCallbackMethod = "onLongClick",
        listenerClazz = View.OnLongClickListener.class
)
public @interface OnLongClick {
    int[] value();
    Class<? extends EventInterceptor>[] interceptors() default {};
    Class<? extends Annotation>[] interceptorsCallback() default {};
}
