package com.ygy.java.concurrent.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 项目名称：java
 * 类名称：CountDownLatchExample1
 * 类描述：
 */
public class CountDownLatchExample2 {

    private static final int threadCount=200;

    public static void main(String[] args) throws Exception {
        ExecutorService exec= Executors.newCachedThreadPool();

        final CountDownLatch countDownLatch=new CountDownLatch(threadCount);
        for (int i=0;i<threadCount;i++){
            final int threadNum=i;
            exec.execute(()->{
                try {
                    test(threadNum);
                } catch (Exception e){
                    e.getMessage();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await(10, TimeUnit.MILLISECONDS);
        System.out.println("finish");
        exec.shutdown();
    }

    private static void test(int threadNum) throws Exception{
        Thread.sleep(100);
        System.out.println(threadNum);
    }
}
