package com.sky.context;

public class BaseContext {
    /**
     * ThreadLocal 用于保存当前线程的上下文信息，获取当前登录用户的ID
     */
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
