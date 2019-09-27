package com.example.providerserver.dtx.task;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Task {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void waitting() {
        try {
            lock.lock();
            condition.await();
            System.out.println("waiting.........");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        System.out.println("release.........");
        lock.lock();
        condition.signal();
        lock.unlock();
    }
}
