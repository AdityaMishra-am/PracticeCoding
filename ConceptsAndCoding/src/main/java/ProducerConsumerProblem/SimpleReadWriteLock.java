package ProducerConsumerProblem;

public class SimpleReadWriteLock {
  private int readers = 0;
  private boolean writerActive = false;

  // Acquire the read lock. Waits if a writer is active.
  public synchronized void lockRead() throws InterruptedException {
    while (writerActive) {
      wait();
    }
    readers++;
  }

  // Release the read lock.
  public synchronized void unlockRead() {
    readers--;
    // If there are no more readers, notify waiting threads.
    if (readers == 0) {
      notifyAll();
    }
  }

  // Acquire the write lock. Waits if either a writer is active or there are active readers.
  public synchronized void lockWrite() throws InterruptedException {
    while (writerActive || readers > 0) {
      wait();
    }
    writerActive = true;
  }

  // Release the write lock.
  public synchronized void unlockWrite() {
    writerActive = false;
    notifyAll();
  }

  // Example usage demonstrating the lock behavior.
  public static void main(String[] args) {
    SimpleReadWriteLock lock = new SimpleReadWriteLock();

    // Reader task
    Runnable readTask = () -> {
      try {
        lock.lockRead();
        System.out.println(Thread.currentThread().getName() + " acquired read lock.");
        Thread.sleep(200); // Simulate read operation
        System.out.println(Thread.currentThread().getName() + " releasing read lock.");
        lock.unlockRead();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    };

    // Writer task
    Runnable writeTask = () -> {
      try {
        lock.lockWrite();
        System.out.println(Thread.currentThread().getName() + " acquired write lock.");
        Thread.sleep(500); // Simulate write operation
        System.out.println(Thread.currentThread().getName() + " releasing write lock.");
        lock.unlockWrite();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    };

    // Start reader and writer threads
    Thread reader1 = new Thread(readTask, "Reader-1");
    Thread reader2 = new Thread(readTask, "Reader-2");
    Thread writer1 = new Thread(writeTask, "Writer-1");

    reader1.start();
    reader2.start();
    writer1.start();
  }
}
