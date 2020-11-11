package com.ygy.java.concurrent.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 项目名称：java
 * 类名称：LockExample6
 * 类描述：
 */
public class LockExample6 {

    public static void main(String[] args) {
        ReentrantLock reentrantLock=new ReentrantLock();
        Condition condition=reentrantLock.newCondition();

        new Thread(()->{
            try{
                reentrantLock.lock();
                System.out.println("wait signal");
                condition.await();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("get signal");
            reentrantLock.unlock();
        }).start();

        new Thread(()->{
            reentrantLock.lock();
            System.out.println("get lock");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            condition.signalAll();
            System.out.println("send signal ~");
            reentrantLock.unlock();
        }).start();
    }
}
