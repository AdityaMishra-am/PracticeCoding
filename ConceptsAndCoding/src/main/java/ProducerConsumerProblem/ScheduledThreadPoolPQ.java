package ProducerConsumerProblem;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduledThreadPoolPQ {
  private final PriorityQueue<ScheduledTask> queue = new PriorityQueue<>();
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private final Thread[] workers;
  private volatile boolean isShutdown = false;

  public ScheduledThreadPoolPQ(int poolSize) {
    workers = new Thread[poolSize];
    for (int i = 0; i < poolSize; i++) {
      workers[i] = new Worker("Worker-" + i);
      workers[i].start();
    }
  }

  public void shutdown() {
    isShutdown = true;
    lock.lock();
    try {
      condition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  public ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
    return schedule(command, delay, -1, unit, false);
  }

  public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return schedule(command, initialDelay, period, unit, true);
  }

  public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return schedule(command, initialDelay, delay, unit, false);
  }

  private ScheduledFuture schedule(Runnable command, long delay, long period, TimeUnit unit, boolean isFixedRate) {
    if (isShutdown) throw new IllegalStateException("Pool is shutting down");

    long delayMillis = TimeUnit.MILLISECONDS.convert(delay, unit);
    long periodMillis = period > 0 ? TimeUnit.MILLISECONDS.convert(period, unit) : -1;
    ScheduledTask task = new ScheduledTask(command,
        System.currentTimeMillis() + delayMillis,
        periodMillis,
        isFixedRate);

    lock.lock();
    try {
      queue.offer(task);
      if (queue.peek() == task) {
        condition.signal();
      }
    } finally {
      lock.unlock();
    }
    return task;
  }

  private class Worker extends Thread {
    public Worker(String name) {
      super(name);
    }

    @Override
    public void run() {
      while (!isShutdown) {
        lock.lock();
        try {
          while (queue.isEmpty() && !isShutdown) {
            condition.await();
          }

          if (isShutdown) return;

          long now = System.currentTimeMillis();
          ScheduledTask task = queue.peek();

          if (task != null && task.scheduledTime <= now) {
            task = queue.poll();
            try {
              lock.unlock();  // Release lock during task execution
              runTask(task);
            } finally {
              lock.lock();    // Re-acquire lock before next iteration
            }
          } else if (task != null) {
            long waitTime = task.scheduledTime - now;
            condition.await(waitTime, TimeUnit.MILLISECONDS);
          }
        } catch (InterruptedException e) {
          // Handle shutdown
        } finally {
          lock.unlock();
        }
      }
    }

    private void runTask(ScheduledTask task) {
      if (task.isCancelled()) return;

      try {
        task.runnable.run();
      } catch (Throwable t) {
        t.printStackTrace();
      }

      if (task.isPeriodic() && !task.isCancelled()) {
        reschedule(task);
      }
    }

    private void reschedule(ScheduledTask task) {
      long now = System.currentTimeMillis();
      if (task.isFixedRate) {
        task.scheduledTime += task.period;
      } else {
        task.scheduledTime = now + task.period;
      }

      lock.lock();
      try {
        queue.offer(task);
        if (queue.peek() == task) {
          condition.signal();
        }
      } finally {
        lock.unlock();
      }
    }
  }

  private static class ScheduledTask implements ScheduledFuture, Comparable<ScheduledTask> {
    final Runnable runnable;
    long scheduledTime;
    final long period;
    final boolean isFixedRate;
    volatile boolean cancelled = false;

    ScheduledTask(Runnable runnable, long scheduledTime, long period, boolean isFixedRate) {
      this.runnable = runnable;
      this.scheduledTime = scheduledTime;
      this.period = period;
      this.isFixedRate = isFixedRate;
    }

    @Override
    public int compareTo(ScheduledTask other) {
      return Long.compare(this.scheduledTime, other.scheduledTime);
    }

    @Override
    public boolean cancel() {
      cancelled = true;
      return true;
    }

    @Override
    public boolean isCancelled() {
      return cancelled;
    }

    boolean isPeriodic() {
      return period > 0;
    }
  }

  public interface ScheduledFuture {
    boolean cancel();
    boolean isCancelled();
  }

  public static void main(String[] args) throws InterruptedException {
    ScheduledThreadPoolPQ executor = new ScheduledThreadPoolPQ(2);

    // One-time task after 1 second
    executor.schedule(() -> log("One-time task"), 1, TimeUnit.SECONDS);

    // Fixed-rate task (every 2 seconds)
    executor.scheduleAtFixedRate(() -> log("Fixed-Rate Task"), 0, 2, TimeUnit.SECONDS);

    // Fixed-delay task (2 seconds between completion and next start)
    executor.scheduleWithFixedDelay(() -> {
      log("Fixed-Delay Task Start");
      try { Thread.sleep(500); } catch (InterruptedException e) {}
      log("Fixed-Delay Task End");
    }, 0, 2, TimeUnit.SECONDS);

    // Let tasks run for 10 seconds
    Thread.sleep(10000);
    executor.shutdown();
  }

  private static void log(String message) {
    String timestamp = new Date().toString();
    System.out.printf("%s [%s] %s\n",
        timestamp,
        Thread.currentThread().getName(),
        message);
  }
}