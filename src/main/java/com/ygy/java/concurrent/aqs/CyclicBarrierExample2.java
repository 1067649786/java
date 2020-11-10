package com.ygy.java.concurrent.aqs;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 项目名称：java
 * 类名称：CyclicBarrierExample
 * 类描述：
 */
public class CyclicBarrierExample2 {

    private static CyclicBarrier barrier=new CyclicBarrier(5);

    public static void main(String[] args) throws Exception {
        ExecutorService executorService= Executors.newCachedThreadPool();

        for (int i=0;i<10;i++){
            final int threadNum=i;
            Thread.sleep(100);
            executorService.execute(()->{
                try{
                    race(threadNum);
                } catch (Exception e){
                    e.getMessage();
                }
            });
        }
        executorService.shutdown();
    }

    private static void race(int threadNum) throws Exception{
        Thread.sleep(1000);
        System.out.println(threadNum+" is ready");
        barrier.await(2000,TimeUnit.MILLISECONDS);
        System.out.println(threadNum+" continue");
    }
}
