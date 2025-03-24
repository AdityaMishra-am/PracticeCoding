package co.practice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;

public class SharedResource {
 public static Queue<Integer> q = new LinkedList<>();
  int CAP;

  public SharedResource(int cap) {
    this.CAP = cap;
  }
  synchronized void produce(int i) throws InterruptedException {
    while (q.size() == CAP) {
      System.out.println("waiting for queue to get empty");
      wait();
    }
      q.add(i);
      System.out.println("added "+ i + "in the queue");
      notify();

  }
  synchronized void consume() throws InterruptedException{
    while (q.isEmpty()) {
      System.out.println("waiting for queue to be non empty");
      wait();
    }
      System.out.println("consumed "+ q.poll() + "from the queue");
      notify();

  }
  int read(ReadWriteLock lock) {
    try {
      lock.readLock().lock();
      System.out.println("taking read lock");
      Thread.sleep(1000);
    } catch (Exception e) {
      //Some exception
    } finally {
      lock.readLock().unlock();
    }
    return this.CAP;
  }
  int write(ReadWriteLock lock, int val) {
    try {
      lock.writeLock().lock();
      System.out.println("taking write lock");
      Thread.sleep(1000);
      this.CAP = val;
    } catch (Exception e) {
      //Some exception
    } finally {
      lock.writeLock().unlock();
    }
    return this.CAP;
  }
  static int counter = 0;
  synchronized  void inc(){
    counter++;
    System.out.println("Value is "+ counter);
  }
}
