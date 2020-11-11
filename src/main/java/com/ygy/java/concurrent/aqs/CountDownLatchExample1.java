package com.ygy.java.concurrent.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch的概念
 *  CountDownLatch是一个同步工具类，用来协调多个线程之间的同步，或者说起到线程之间通信（而不是用作互斥的作用）
 *  CountDownLatch能够使一个线程在等待另外一些线程完成各自工作之后，再继续执行。使用一个计数器实现。计数器初始值为线程的数量。当每一个线
 *  程完成自己的任务后，计数器的值会减一。当计数器的值为0时，表示所有的线程都已经完成了一些任务，然后在CountDownLatch上等待的线程就可以
 *  恢复执行接下来的任务
 *
 * CountDownLatch的用法
 *  1、某一线程在开始运行前等待n个线程执行完毕。将CountDownLatch的计数器初始化为new CountDownLatch(n),每当一个任务线程执行完毕，就将
 *  计数器减一，countdownLatch.countDown(),当计数器的值变为0时，在CountDownLatch上await()的线程就会被唤醒。一个典型应用场景就是
 *  启动一个服务时，主线程需要等待多个组件加载完毕，之后再继续进行。
 *  2、实现多个线程开始执行任务的最大并行性。注意是并行性，不是并发，强调的是多个线程在某一时刻同时开始执行。类似于赛跑，将多个线程放在起点，
 *  等待发令枪响，然后同时开跑。做法是初始化一个共享的CountDownLatch(1)，将其计数器初始化为1，多个线程在开始执行任务前首先
 *  countdownlatch.await(),当主线程调用countDown()时，计数器变为0，多个线程同时被唤醒。
 *
 * CountDownLatch的不足
 *  CountDownLatch时一次性的，计数器的值只能在构造方法中初始化一次，之后没有任何机制再对其设置值，当CountDownLatch使用完毕后，
 *  它不能再被使用
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
