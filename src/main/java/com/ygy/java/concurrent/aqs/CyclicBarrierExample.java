package com.ygy.java.concurrent.aqs;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CyclicBarrier的字面意思是可循环使用的屏障，类似于CountDownLatch也是个计数器，不同的是CyclicBarrier要做的事情是，让一组线程
 * 到达一个屏障(也可以叫同步点)时被阻塞，直到最后一个线程到达屏障时，屏障才会开门，所有被屏障拦截的线程才会继续干活。之所有用循环修饰，
 * 是因为在所有的线程释放彼此之后，这个屏障是可以重新使用的(reset()方法重置屏障点)
 *
 * CyclicBarrier是一种同步机制，允许一组线程相互等待，等到所有线程都到达一个屏障点才退出await()方法，它没有直接实现AQS，而是借助
 * ReentrantLock来实现的同步机制。它是可循环使用的，而CountDownLatch是一次性的，另外它体现的语义也跟CountDownLatch不同，
 * CountDownLatch减少计数到达条件采用的是release方式，而CyclicBarrier走向屏障点(await)采用的是Acquire方式，Acquire是会阻塞的
 * 这也实现了CyclicBarrier的另外一个特点，只要有一个线程中断，那么屏障点就被打破，所有线程都将被唤醒(CyclicBarrier自己负责这部分实现，
 * 不是由AQS调度的)，这样也避免了因为一个线程中断引起永远不能到达屏障点而导致其他线程一直等待。屏障点被打破的CyclicBarrier将不可再使用
 * (会抛出BrokenBarrierException)，除非执行reset操作
 *
 * CountDownLatch：一个或多个线程，等待其他多个线程完成某件事之后才能执行
 * CyclicBarrier：多个线程相互等待，直到到达同一个同步点，再继续一起执行
 */
public class CyclicBarrierExample {

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
    }

    private static void race(int threadNum) throws Exception{
        Thread.sleep(1000);
        System.out.println(threadNum+" is ready");
        barrier.await();
        System.out.println(threadNum+" continue");
    }
}
