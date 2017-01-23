package org.bar.flogj;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Controller implements Runnable {
    // queue and it's lock
    private Queue<Record> queue = new LinkedList<Record>();
    private int maxSize = 100 * 1024 * 1024;
    private Lock lock = new ReentrantLock();
    private Condition full = lock.newCondition();
    private Condition empty = lock.newCondition();

    // writer thread and it's stop flag
    private Writer writer = null;
    private Thread thread = new Thread(this);
    private int flag = 1;

    Controller(Writer writer) {
        this.writer = writer;
    }

    /**
     * put a record into a queue
     * @param level
     * @param msg
     */
    boolean put(Level level, String msg) {
        // new record
        Record record = new Record(level, msg);

        try {
            // lock
            this.lock.lock();

            // return if stop flag is set
            if (this.flag == 1) {
                return false;
            }

            // if queue is full wait until it's not full
            while (this.queue.size() >= maxSize) {
                try {
                    full.await(5, TimeUnit.SECONDS);
                    if (this.flag == 1) {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // add queue
            this.queue.offer(record);

            // signal wait thread if queue is empty
            if (this.queue.size() == 1) {
                empty.signal();
            }
        } finally {
            // unlock
            this.lock.unlock();
        }

        return true;
    }

    /**
     * get data queue
     * @return
     */
    Queue<Record> getQueue() {
        Queue<Record> emptyQueue = new LinkedList<Record>();

        try {
            // lock
            this.lock.lock();
            if (this.flag == 1 && this.queue.size() == 0) {
                return null;
            }

            // if queue is empty wait at most 5 seconds
            if (this.queue.size() <= 0) {
                try {
                    this.empty.await(5, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // get data queue by exchange the two queue
            Queue<Record> dataQueue = this.queue;
            this.queue = emptyQueue;

            // if queue is empty signal all waiting thread
            if (dataQueue.size() >= this.maxSize) {
                full.signalAll();
            }

            return dataQueue;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * start writer thread
     */
    boolean start() {
        // start collector thread
        if (!this.writer.start()) {
            System.out.println("start collector thread failed");
            return false;
        }

        // start the writer thread
        this.thread.start();
        return true;
    }

    /**
     * stop writer thread
     */
    void stop() {
        try {
            // set stop flag
            try {
                this.lock.lock();
                this.flag = 1;
                this.empty.signal();
            } finally {
                this.lock.unlock();
            }

            // wait write thread exit
            this.thread.join();

            // stop collector thread
            this.writer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * writer thread function
     */
    public void run() {
        // unset stop flag
        try {
            this.lock.lock();
            this.flag = 0;
        } finally {
            this.lock.unlock();
        }

        // handle data in a while loop
        while (true) {
            // get data queue and handle it
            Queue<Record> queue = this.getQueue();
            if (queue == null) {
                break;
            }
            this.writer.write(queue);
        }
    }
}
