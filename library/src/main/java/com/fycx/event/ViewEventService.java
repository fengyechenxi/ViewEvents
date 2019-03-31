package com.fycx.event;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.fycx.event.annotation.ViewEvent;
import com.fycx.event.interceptor.EventInterceptor;
import com.fycx.event.interceptor.InterceptorWrapper;
import com.fycx.event.proxy.ListenerInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * View的事件控制器
 */
public class ViewEventService {

    /**
     * 允许重复点击的间隔时间
     */
    private static final long EVENT_RESPONSE_INTERVAL = 400;
    /**
     * 拦截重复事件的间隔时间
     */
    private long mRepeatInterval = ViewEventService.EVENT_RESPONSE_INTERVAL;

    public void injectEvents(Object target){
        Class<?> clazz = target.getClass();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            //获取方法上的注解
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                //获取注解的class对象
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (annotationType != null) {
                    // 获取该注解上的注解ViewEvent，只有该注解上有ViewEvent注解，才是我们需要的注解
                    ViewEvent viewEvent = annotationType.getAnnotation(ViewEvent.class);
                    if (viewEvent != null) {
                        //获取元注解的内定义的方法的数据值
                        String callbackMethod = viewEvent.listenerCallbackMethod();
                        String setMethod = viewEvent.listenerSetMethod();
                        Class listenerClazz = viewEvent.listenerClazz();
                        try {
                            //获取事件注解的value方法值
                            int[] viewIds = getViewIds(annotation,annotationType);
                            //解析注解中配置的拦截事件数据，并封装返回
                            List<InterceptorWrapper> wrappers = parseInterceptors(methods,annotation,annotationType);
                            //创建监听器动态代理类
                            ListenerInvocationHandler handler = new ListenerInvocationHandler(target);
                            handler.addMethod(callbackMethod,method);
                            handler.setInterceptors(wrappers);
                            handler.setEventInterval(mRepeatInterval);
                            Object listener = Proxy.newProxyInstance(listenerClazz.getClassLoader(), new Class[]{listenerClazz}, handler);
                            //给注解上添加了id值的每一个View设置注解相应的监听器
                            setListeners(target,viewIds,listener,setMethod,listenerClazz);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void setListeners(Object target, int[] viewIds, Object listener, String setMethod, Class listenerClazz) {
        for (int viewId : viewIds) {
            View view = findViewById(target,viewId);
            if (view != null) {
                try {
                    Method setter = view.getClass().getMethod(setMethod, listenerClazz);
                    setter.invoke(view,listener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private View findViewById(Object target, int viewId){
        if(target instanceof Activity){
            Activity activity = (Activity) target;
            return activity.findViewById(viewId);
        }
        else if(target instanceof Fragment){
            Fragment fragment = (Fragment) target;
            View rootView = fragment.getView();
            if (rootView == null) {
                return null;
            }
            return rootView.findViewById(viewId);
        }
        else if(target instanceof android.app.Fragment){
            android.app.Fragment fragment = (android.app.Fragment) target;
            View rootView = fragment.getView();
            if (rootView == null) {
                return null;
            }
            return rootView.findViewById(viewId);
        }
        else {
            throw new IllegalArgumentException("target must be instance of Activity or Fragment");
        }
    }

    private List<InterceptorWrapper> parseInterceptors(Method[] methods, Annotation annotation, Class<? extends Annotation> annotationType) {
        //获取事件注解的interceptors方法值
        Class<? extends EventInterceptor>[] interceptors = getInterceptors(annotation,annotationType);
        //获取事件注解的interceptorsCallback方法值
        Class<? extends Annotation>[] callbacks = getInterceptorsCallback(annotation,annotationType);
//        //如果数据为null，抛出异常
        if(interceptors == null || callbacks == null){
            throw new RuntimeException(annotationType.getName() + "parse failed");
        }
        if(interceptors.length != callbacks.length){
            throw new IllegalArgumentException("interceptors length must equal to interceptorsCallback length");
        }
        List<InterceptorWrapper> wrappers = new ArrayList<>();
        for (int i = 0; i < callbacks.length; i++) {
            Class<? extends Annotation> annoClazz = callbacks[i];
            List<Method> annoMethods = new ArrayList<>();
            for (Method m : methods) {
                Annotation anno = m.getAnnotation(annoClazz);
                if (anno != null) {
                    //添加
                    annoMethods.add(m);
                }
            }
            InterceptorWrapper wrapper = new InterceptorWrapper(interceptors[i],annoMethods);
            wrappers.add(wrapper);
        }
        return wrappers;
    }

    private int[] getViewIds(Annotation annotation,Class<? extends Annotation> annotationType) {
        try {
            Method valueMethod = annotationType.getDeclaredMethod("value");
            //调用方法，获取值，也就是获得view的id值
            return  (int[]) valueMethod.invoke(annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取事件注解的interceptors方法值
    private Class<? extends EventInterceptor>[] getInterceptors(Annotation annotation,Class<? extends Annotation> annotationType) {
        try {
            Method interceptorsMethod = annotationType.getDeclaredMethod("interceptors");
            return  (Class<? extends EventInterceptor>[]) interceptorsMethod.invoke(annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //获取事件注解的interceptorsCallback方法值
    private Class<? extends Annotation>[] getInterceptorsCallback(Annotation annotation,Class<? extends Annotation> annotationType) {
        try {
            Method interceptorsCallbackMethod = annotationType.getDeclaredMethod("interceptorsCallback");
            return  (Class<? extends Annotation>[]) interceptorsCallbackMethod.invoke(annotation);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ViewEventService setRepeatInterval(long repeatInterval) {
        mRepeatInterval = repeatInterval;
        return this;
    }
}
