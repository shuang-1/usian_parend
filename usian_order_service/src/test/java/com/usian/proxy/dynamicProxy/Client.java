package com.usian.proxy.dynamicProxy;

import java.lang.reflect.Proxy;

public class Client {

    public static void main(String[] args) {
        Star realStar = new RealStar();
        ProxyStar proxyStar = new ProxyStar(realStar);
        //Star代理的接口  proxyStar代理类要做的事情
        Star proxy = (Star) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Star.class}, proxyStar);

        proxy.sing();
        proxy.bookTicket();
    }
}
