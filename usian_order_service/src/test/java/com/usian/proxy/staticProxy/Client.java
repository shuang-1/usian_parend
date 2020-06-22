package com.usian.proxy.staticProxy;

public class Client {
    public static void main(String[] args) {
        Star realStar = new RealStar();
        Star proxyStar = new ProxyStar(realStar);

        proxyStar.sing();
        proxyStar.bookTicket();
        proxyStar.collectMoney();
        proxyStar.confer();
        proxyStar.signContract();
    }
}
