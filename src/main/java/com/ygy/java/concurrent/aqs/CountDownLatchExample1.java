package com.ygy.java.concurrent.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch自身维护了一个计数器，每调用一次countDown()方法，计数器的值减一(原子操作)
 * 当计数器的值减到0时，才会运行await()方法后面的代码
 */
public class CountDownLatchExample1 {

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
        countDownLatch.await();
        System.out.println("finish");
        exec.shutdown();
    }

    private static void test(int threadNum) throws Exception{
        Thread.sleep(100);
        System.out.println(threadNum);
        Thread.sleep(100);
    }
}
