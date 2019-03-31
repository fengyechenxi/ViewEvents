package com.fycx.event.proxy;

import android.util.Log;

import com.fycx.event.interceptor.EventInterceptor;
import com.fycx.event.interceptor.InterceptorWrapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenerInvocationHandler implements InvocationHandler {

    public static final String TAG = "ListenerInvocation";

    private Object mTarget;
    private Map<String,Method> mMethodMap = new HashMap<>();

    private List<InterceptorWrapper> mInterceptors;

    private long mLastClickTime;
    /**
     * 拦截重复事件的间隔时间
     */
    private long mEventInterval;

    public ListenerInvocationHandler(Object target){
        mTarget = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //先判断点击间隔
        if(intervalEnough()){
            if(mTarget != null){
                //判断自定义拦截器
                if (mInterceptors != null && mInterceptors.size() > 0) {
                    for (InterceptorWrapper wrapper : mInterceptors) {
                        Class<? extends EventInterceptor> interceptorClazz = wrapper.getInterceptor();
                        //如果传入的拦截器是EventInterceptor子类，则才可以进行拦截判断
                        if(EventInterceptor.class.isAssignableFrom(interceptorClazz)){
                            EventInterceptor interceptor = interceptorClazz.newInstance();
                            boolean intercept = interceptor.interceptRule();
                            if(intercept){
                                //如果满足拦截的条件，下面所有的执行都不进行，并且回调拦截配置的方法，直接返回null，
                                List<Method> callbackMethods = wrapper.getCallbacks();
                                for (Method callbackMethod : callbackMethods) {
                                    callbackMethod.invoke(mTarget);
                                }
                                //获取method的返回值,有些方法是由返回值的
                                Type genericReturnType = method.getGenericReturnType();
                                String strReturn = genericReturnType.toString();
                                Log.e(TAG,"strReturn:"+strReturn);
                                if(strReturn.equals("boolean")){
                                    return true;
                                }
                                else {
                                    return null;
                                }
                            }
                        }
                    }
                }

                String methodName = method.getName();
                //将原本动态代理要调用的方法替换成我们注解标记的方法，这样原来的方法就不会调用，而是调用我们设计的流程
                method = mMethodMap.get(methodName);
                if (method != null) {
                    return method.invoke(mTarget,args);
                }
            }
        }
        return null;
    }

    public void addMethod(String methodName,Method method){
        mMethodMap.put(methodName,method);
    }

    public void setInterceptors(List<InterceptorWrapper> interceptors) {
        mInterceptors = interceptors;
    }

    private boolean intervalEnough(){
        long now = System.currentTimeMillis();
        boolean enough = now - mLastClickTime > mEventInterval;
        mLastClickTime = now;
        return enough;
    }

    public void setEventInterval(long eventInterval) {
        mEventInterval = eventInterval;
    }
}
