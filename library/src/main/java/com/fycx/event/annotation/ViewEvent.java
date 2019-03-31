package com.fycx.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewEvent {
    //事件监听器的添加方法
    String listenerSetMethod();
    //事件监听器的监听器的class类对象
    Class  listenerClazz();
    //事件监听器回调方法的方法名称
    String listenerCallbackMethod();
}
