package com.example.consumerserver.dtx.task;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Task {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void waitting() {
        System.out.println("commit.................");
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        System.out.println("release.................");
        lock.lock();
        condition.signal();
        lock.unlock();
    }
}
