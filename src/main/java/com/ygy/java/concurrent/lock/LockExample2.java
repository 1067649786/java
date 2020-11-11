package com.ygy.java.concurrent.lock;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * 项目名称：java
 * 类名称：LockExample1
 * 类描述：
 */
public class LockExample2 {

    private final Map<String,Data> map=new TreeMap<>();

    private final ReentrantReadWriteLock lock=new ReentrantReadWriteLock();

    private final Lock readLock=lock.readLock();

    private final Lock writeLock=lock.writeLock();

    public Data get(String key){
        readLock.lock();
        try {
            return map.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public Set<String> getAllKeys(){
        readLock.lock();
        try {
            return map.keySet();
        } finally {
            readLock.unlock();
        }
    }

    public Data put(String key,Data value){
        writeLock.lock();
        try {
            return map.put(key,value);
        } finally {
            writeLock.unlock();
        }
    }

    class Data {

    }
}
