package com.fycx.event.interceptor;

import java.lang.reflect.Method;
import java.util.List;

public class InterceptorWrapper {

    private Class<? extends EventInterceptor> interceptor;
    private List<Method> callbacks;

    public InterceptorWrapper(Class<? extends EventInterceptor> interceptor, List<Method> callbacks) {
        this.interceptor = interceptor;
        this.callbacks = callbacks;
    }

    public Class<? extends EventInterceptor> getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Class<? extends EventInterceptor> interceptor) {
        this.interceptor = interceptor;
    }

    public List<Method> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(List<Method> callbacks) {
        this.callbacks = callbacks;
    }
}
