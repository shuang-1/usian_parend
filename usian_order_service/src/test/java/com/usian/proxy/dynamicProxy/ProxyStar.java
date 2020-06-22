package com.usian.proxy.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ProxyStar implements InvocationHandler {

    private Object realStar;

    public ProxyStar(Object object){
        this.realStar = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        System.out.println("真正的方法执行前！");
        System.out.println("面谈，签合同，预付款，订机票");
        Object invoke = method.invoke(realStar, args);
        System.out.println("真正的方法执行后！");
        System.out.println("收尾款");

        return invoke;
    }
}
