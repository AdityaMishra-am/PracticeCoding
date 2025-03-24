package ProducerConsumerProblem;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyReentrantLock implements Lock {
  private Thread owner;      // The thread that currently holds the lock
  private int state;         // The hold count; state > 0 means the lock is held

  public MyReentrantLock() {
    this.owner = null;
    this.state = 0;
  }

  @Override
  public void lock() {
    Thread current = Thread.currentThread();
    synchronized (this) {
      if (owner == current) {
        state++;
        return;
      }
      // If lock is held by another thread, wait until it's free
      while (state != 0) {
        try {
          wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          // In a production environment, handle interruption more robustly
        }
      }
      // Acquire the lock
      owner = current;
      state = 1;
    }
  }

  @Override
  public void unlock() {
    synchronized (this) {
      if (owner != Thread.currentThread()) {
        throw new IllegalMonitorStateException("Only the owner can unlock");
      }
      state--;
      if (state == 0) {
        owner = null;
        notifyAll(); // Wake up waiting threads
      }
    }
  }

  @Override
  public Condition newCondition() {
    return new ConditionObject();
  }

  // Helper method to check if the current thread holds the lock
  private boolean isHeldByCurrentThread() {
    return owner == Thread.currentThread();
  }

  // Internal method to acquire the lock with a specific hold count
  private void acquire(int newState) {
    Thread current = Thread.currentThread();
    synchronized (this) {
      while (state != 0) {
        try {
          wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      owner = current;
      state = newState;
    }
  }

  // Internal method to release the lock completely
  private void unlockCompletely() {
    synchronized (this) {
      state = 0;
      owner = null;
      notifyAll();
    }
  }

  // Nested Condition implementation
  private class ConditionObject implements Condition {
    private final Object conditionMonitor = new Object(); // Monitor for this condition

    @Override
    public void await() throws InterruptedException {
      if (!isHeldByCurrentThread()) {
        throw new IllegalMonitorStateException("Lock not held by current thread");
      }
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      // Save the current hold count and release the lock
      int savedState = state;
      unlockCompletely();
      // Wait on the condition's monitor
      synchronized (conditionMonitor) {
        conditionMonitor.wait();
      }
      // Reacquire the lock with the saved hold count
      acquire(savedState);
    }

    @Override
    public void signal() {
      synchronized (this) {
        if (!isHeldByCurrentThread()) {
          throw new IllegalMonitorStateException("Lock not held by current thread");
        }
      }
      synchronized (conditionMonitor) {
        conditionMonitor.notify();
      }
    }

    @Override
    public void signalAll() {
      synchronized (this) {
        if (!isHeldByCurrentThread()) {
          throw new IllegalMonitorStateException("Lock not held by current thread");
        }
      }
      synchronized (conditionMonitor) {
        conditionMonitor.notifyAll();
      }
    }

    // Additional Condition methods (not implemented for simplicity)
    @Override public void awaitUninterruptibly() { /* Not implemented */ }
    @Override public long awaitNanos(long nanosTimeout) throws InterruptedException { return 0; /* Not implemented */ }
    @Override public boolean await(long time, java.util.concurrent.TimeUnit unit) throws InterruptedException { return false; /* Not implemented */ }
    @Override public boolean awaitUntil(java.util.Date deadline) throws InterruptedException { return false; /* Not implemented */ }
  }

  // Methods not implemented for this basic version
  @Override public void lockInterruptibly() throws InterruptedException { /* Not implemented */ }
  @Override public boolean tryLock() { return false; /* Not implemented */ }
  @Override public boolean tryLock(long time, java.util.concurrent.TimeUnit unit) throws InterruptedException { return false; /* Not implemented */ }


  public static void main(String[] args) {
    MyReentrantLock lock = new MyReentrantLock();
    Condition condition = lock.newCondition();

    Runnable task = () -> {
      lock.lock();
      try {
        System.out.println(Thread.currentThread().getName() + " acquired lock");
        lock.lock(); // Reentrant acquisition
        System.out.println(Thread.currentThread().getName() + " acquired lock again");
        condition.await(); // Releases lock, waits, then reacquires
        System.out.println(Thread.currentThread().getName() + " resumed after await");
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        lock.unlock();
        lock.unlock(); // Must unlock twice due to reentrancy
      }
    };

    Thread t1 = new Thread(task, "Thread-1");
    Thread t2 = new Thread(() -> {
      lock.lock();
      try {
        System.out.println("Thread-2 signaling");
        condition.signal();
      } finally {
        lock.unlock();
      }
    }, "Thread-2");

    t1.start();
    t2.start();
  }
}