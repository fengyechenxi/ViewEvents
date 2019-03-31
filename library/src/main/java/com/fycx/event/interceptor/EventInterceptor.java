package com.fycx.event.interceptor;

/**
 * 事件拦截器
 */
public interface EventInterceptor {
    /**
     * 自定义拦截规则
     * @return 是否拦截，true代表拦截，false代表不拦截
     */
    boolean interceptRule();
}
