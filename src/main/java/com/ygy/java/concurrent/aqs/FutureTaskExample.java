package com.ygy.java.concurrent.aqs;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 项目名称：java
 * 类名称：FutureTaskExample
 * 类描述：
 */
public class FutureTaskExample {

    public static void main(String[] args) throws Exception {
        FutureTask<String> futureTask=new FutureTask<>(() -> {
            System.out.println("do something in callable");
            Thread.sleep(5000);
            return "Done";
        });
        new Thread(futureTask).start();
        System.out.println("do something in main");
        Thread.sleep(1000);
        String result=futureTask.get();
        System.out.println("result:"+result);
    }
}
