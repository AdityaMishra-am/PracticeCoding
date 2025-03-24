package ProducerConsumerProblem;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class ConsumerProducer {
  Queue<Integer> queue;
  int cap;

  public ConsumerProducer(int cap) {
    this.cap = cap;
    queue = new LinkedList<>();
  }

  public synchronized void producer(int i) {
    if (queue.size() == cap) {
      try{
        System.out.println("waiting for queue to get empty");
        wait();
      } catch (InterruptedException e) {
        //some handli
      }
    }
    if (queue.isEmpty()) {
      queue.add(i);
      notifyAll();
    }
    else  {
      queue.add(i);
    }
  }
  public  synchronized int consumer() {
    if (queue.isEmpty()) {
      try {
        System.out.println("waiting for queue to be non empty " + Thread.currentThread().getName());
        wait();
      } catch (InterruptedException e) {
        //some handling
      }
    }
      int consumed = queue.poll();
      notify();
      return consumed;

  }
}
