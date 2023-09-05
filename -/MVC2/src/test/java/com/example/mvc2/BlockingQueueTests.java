package com.example.mvc2;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {
    public static void main(String[] args) {
            //生产消费公用一个队列
        BlockingQueue queue = new ArrayBlockingQueue(10);
        //一个生产者，三个消费者
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

class Producer implements Runnable {

    private BlockingQueue<Integer> queue;
    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            for(int i = 0 ;i < 100;i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread() + "生产" + queue.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;
    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            //一直获取直到队列空
            while(true) {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费" + queue.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
