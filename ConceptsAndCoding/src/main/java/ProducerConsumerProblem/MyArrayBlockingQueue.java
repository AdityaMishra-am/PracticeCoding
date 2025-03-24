package ProducerConsumerProblem;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyArrayBlockingQueue<E> {
  final Object[] items;
  final int capacity;
  private int head = 0;
  private int tail = 0;
  private int count = 0;
  private final Condition notEmpty;
  private final Condition notFull;
  MyReentrantLock reentrantLock = new MyReentrantLock();
  public MyArrayBlockingQueue(int capacity) {
    this.items = new Object[capacity];
    this.capacity =capacity;
    notEmpty = reentrantLock.newCondition();
    notFull = reentrantLock.newCondition();
  }

  public void put(E obj) throws InterruptedException {
    reentrantLock.lock();
    try {
      if (count == capacity) {
        notFull.await();
      }
      items[tail] = obj;
      tail = (tail+1)%capacity;
      notEmpty.signal();
      count++;
    } finally {
      reentrantLock.unlock();
    }
  }

  public boolean offer(E obj)  {
    reentrantLock.lock();
    try {
      if (count == capacity) {
        return false;
      }
      items[tail] = obj;
      tail = (tail+1)%capacity;
      notEmpty.signal();
      count++;
      return true;
    } finally {
      reentrantLock.unlock();
    }
  }
  public E poll() throws InterruptedException {
    reentrantLock.lock();
    try {
      if (count == 0) {
        notEmpty.await();
      }
      E item = (E) items[head];
      items[head] = null;
      head = (head+1)%capacity;
      notFull.signal();
      count--;
      return item;
    } finally {
      reentrantLock.unlock();

    }
  }
  public int size() {
    reentrantLock.lock();
    try {
      return count;
    } finally {
      reentrantLock.unlock();
    }
  }
  public static void main(String[] args) {
    MyArrayBlockingQueue<Integer> queue = new MyArrayBlockingQueue<>(5);

    // Producer thread: puts numbers into the queue.
    Thread producer = new Thread(() -> {
      try {
        for (int i = 0; i < 10; i++) {
          queue.put(i);
          System.out.println("Produced: " + i);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    // Consumer thread: takes numbers from the queue.
    Thread consumer = new Thread(() -> {
      try {
        for (int i = 0; i < 10; i++) {
          int value = queue.poll();
          Thread.sleep(50);
          System.out.println("Consumed: " + value);
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    producer.start();
    consumer.start();
  }

}
