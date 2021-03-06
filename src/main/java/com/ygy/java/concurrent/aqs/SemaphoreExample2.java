package com.ygy.java.concurrent.aqs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreExample2 {

    private static final int threadCount=20;

    public static void main(String[] args) throws Exception {
        ExecutorService exec= Executors.newCachedThreadPool();

        final Semaphore semaphore=new Semaphore(3);

        for (int i=0;i<threadCount;i++){
            final int threadNum=i;
            exec.execute(()->{
                try {
                    semaphore.acquire(3);//获取一个许可
                    test(threadNum);
                    semaphore.release(3);//释放一个许可
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
