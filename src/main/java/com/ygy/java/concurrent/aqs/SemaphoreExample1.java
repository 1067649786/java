package com.ygy.java.concurrent.aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 初始化一个Semaphore需要赋初值，代表许可证的总数
 * 调用acquire()方法获取一个或多个许可
 * 调用release()方法释放一个或多个许可
 * 只有当获取了许可的线程才能执行后面的代码
 */
public class SemaphoreExample1 {

    private static final int threadCount=200;

    public static void main(String[] args) throws Exception {
        ExecutorService exec= Executors.newCachedThreadPool();

        final Semaphore semaphore=new Semaphore(20);

        for (int i=0;i<threadCount;i++){
            final int threadNum=i;
            exec.execute(()->{
                try {
                    semaphore.acquire();//获取一个许可
                    test(threadNum);
                    semaphore.release();//释放一个许可
                } catch (Exception e){
                    e.getMessage();
                }
            });
        }
        System.out.println("finish");
        exec.shutdown();
    }

    private static void test(int threadNum) throws Exception{
        System.out.println(threadNum);
        Thread.sleep(1000);
    }
}
