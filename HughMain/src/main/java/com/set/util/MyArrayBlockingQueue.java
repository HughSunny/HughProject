package com.set.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程安全的Queue
 * Created by Hugh on 2016/8/21.
 */
public class MyArrayBlockingQueue<T> {
    private  T[] items ;
    private final Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();
    private int head;
    private int tail;
    private int count;
    public MyArrayBlockingQueue(int maxSize) {
        items = (T[]) new Object[maxSize];
    }

    public MyArrayBlockingQueue() {
        this(10);
    }

    public void put(T t) {
        lock.lock();
        try {
            while (count == getCapactiy()) {
                System.out.println("数据已满，等待");
                notFull.await();
            }
            items[tail] = t;
            if (++tail == getCapactiy()) {
                tail = 0;
            }
            ++count;
            notEmpty.signalAll();//唤醒等待数据线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
             lock.unlock();
        }
    }

    public T take() {
        lock.lock();
        try {
            while (count == 0) {
                System.out.println("没有数据，等待");
                notFull.await();
            }
            T ret = items[head];
            items[head] = null;
            if (++head == getCapactiy()) {
                head = 0;
            }
            --count;
            notFull.signalAll();//唤醒添加数据线程
            return ret;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    private int getCapactiy() {
        return items.length;
    }

    public int size(){
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
