package ProducerConsumerProblem;

import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduledThreadPoolBPQ {
  private final PriorityBlockingQueue<ScheduledTask> queue = new PriorityBlockingQueue<>();
  private final Thread[] workers;
  private final AtomicBoolean isShutdown = new AtomicBoolean(false);

  public ScheduledThreadPoolBPQ(int poolSize) {
    workers = new Thread[poolSize];
    for (int i = 0; i < poolSize; i++) {
      workers[i] = new Worker("Worker-" + (i + 1));
      workers[i].start();
    }
  }

  public ScheduledFuture schedule(Runnable command, long delay, TimeUnit unit) {
    return schedule(command, delay, 0, unit, false);
  }

  public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return schedule(command, initialDelay, period, unit, true);
  }

  public ScheduledFuture scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return schedule(command, initialDelay, delay, unit, false);
  }

  private ScheduledFuture schedule(Runnable command, long delay, long period, TimeUnit unit, boolean isFixedRate) {
    if (isShutdown.get()) throw new IllegalStateException("Executor is shutdown");

    long now = System.currentTimeMillis();
    long initialDelayMillis = TimeUnit.MILLISECONDS.convert(delay, unit);
    long periodMillis = TimeUnit.MILLISECONDS.convert(period, unit);

    ScheduledTask task = new ScheduledTask(
        command,
        now + initialDelayMillis,
        periodMillis,
        isFixedRate
    );

    queue.put(task);
    return task;
  }

  public void shutdown() {
    isShutdown.set(true);
    for (Thread worker : workers) {
      worker.interrupt();
    }
  }

  private class Worker extends Thread {
    public Worker(String name) {
      super(name);
    }

    @Override
    public void run() {
      while (!isShutdown.get()) {
        try {
          ScheduledTask task = queue.take();

          if (task.isCancelled()) continue;

          long delay = task.getDelay(TimeUnit.MILLISECONDS);
          if (delay > 0) {
            queue.put(task);
            Thread.sleep(delay);
            continue;
          }

          task.run();

          if (task.isPeriodic() && !task.isCancelled()) {
            task.reschedule();
            queue.put(task);
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private static class ScheduledTask implements Delayed, Runnable, ScheduledFuture {
    private final Runnable command;
    private volatile long scheduledTime;
    private final long period;
    private final boolean isFixedRate;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    ScheduledTask(Runnable command, long scheduledTime, long period, boolean isFixedRate) {
      this.command = command;
      this.scheduledTime = scheduledTime;
      this.period = period;
      this.isFixedRate = isFixedRate;
    }

    @Override
    public void run() {
      if (!cancelled.get()) {
        try {
          command.run();
        } finally {
          if (!isPeriodic()) {
            cancelled.set(true);
          }
        }
      }
    }

    void reschedule() {
      if (isFixedRate) {
        scheduledTime += period;
      } else {
        scheduledTime = System.currentTimeMillis() + period;
      }
    }

    @Override
    public long getDelay(TimeUnit unit) {
      return unit.convert(scheduledTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
      return Long.compare(scheduledTime, ((ScheduledTask) other).scheduledTime);
    }

    @Override
    public boolean cancel() {
      return cancelled.compareAndSet(false, true);
    }

    @Override
    public boolean isCancelled() {
      return cancelled.get();
    }

    boolean isPeriodic() {
      return period > 0;
    }
  }

  // Interfaces
  public interface ScheduledFuture {
    boolean cancel();
    boolean isCancelled();
  }

  public static void main(String[] args) throws InterruptedException {
    ScheduledThreadPool executor = new ScheduledThreadPool(2);

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
    System.out.printf("%s [%s] %s%n",
        new Date().toString(),
        Thread.currentThread().getName(),
        message);
  }
}

interface Delayed extends Comparable<Delayed> {
  long getDelay(TimeUnit unit);
}