package ProducerConsumerProblem;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {
  private Queue<T> queue = new LinkedList<>();
  private int capacity;

  // Initialize the blocking queue with a fixed capacity
  public BlockingQueue(int capacity) {
    this.capacity = capacity;
  }

  // Add an element to the queue. Blocks if the queue is full.
  public synchronized void put(T item) throws InterruptedException {
    // While the queue is full, wait for space to become available.
    while (queue.size() == capacity) {
      wait();
    }
    // Add the item and notify any waiting threads.
    queue.add(item);
    notifyAll();
  }

  // Remove and return an element from the queue. Blocks if the queue is empty.
  public synchronized T take() throws InterruptedException {
    // While the queue is empty, wait for an element to be added.
    while (queue.isEmpty()) {
      wait();
    }
    T item = queue.remove();
    // Notify all waiting threads that space is now available.
    notifyAll();
    return item;
  }

  // Optional: Get the current size of the queue.
  public synchronized int size() {
    return queue.size();
  }

  // Example usage with producer and consumer threads
  public static void main(String[] args) {
    BlockingQueue<Integer> bq = new BlockingQueue<>(5);

    // Producer thread adds elements to the queue.
    Thread producer = new Thread(() -> {
      for (int i = 1; i <= 10; i++) {
        try {
          bq.put(i);
          System.out.println("Produced: " + i);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    // Consumer thread takes elements from the queue.
    Thread consumer = new Thread(() -> {
      for (int i = 1; i <= 10; i++) {
        try {
          int item = bq.take();
          System.out.println("Consumed: " + item);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    producer.start();
    consumer.start();

    // Wait for threads to finish
    try {
      producer.join();
      consumer.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
