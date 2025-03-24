package ProducerConsumerProblem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScheduledThreadPool {
  private final DelayQueue<ScheduledTask> queue = new DelayQueue<>();
  private final List<WorkerThread> workers = new ArrayList<>();
  private volatile boolean isShutdown = false;

  public ScheduledThreadPool(int poolSize) {
    for (int i = 0; i < poolSize; i++) {
      WorkerThread worker = new WorkerThread(queue);
      workers.add(worker);
      worker.start();
    }
  }

  public void shutdown() {
    isShutdown = true;
    workers.forEach(Thread::interrupt);
  }

  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    if (isShutdown) {
      throw new IllegalStateException("Executor is shutdown");
    }
    long delayMillis = unit.toMillis(delay);
    ScheduledTask task = new ScheduledTask(command, delayMillis, 0, false);
    queue.add(task);
    return task;
  }

  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    if (isShutdown) {
      throw new IllegalStateException("Executor is shutdown");
    }
    long initialDelayMillis = unit.toMillis(initialDelay);
    long periodMillis = unit.toMillis(period);
    ScheduledTask task = new ScheduledTask(command, initialDelayMillis, periodMillis, true);
    queue.add(task);
    return task;
  }

  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    if (isShutdown) {
      throw new IllegalStateException("Executor is shutdown");
    }
    long initialDelayMillis = unit.toMillis(initialDelay);
    long delayMillis = unit.toMillis(delay);
    ScheduledTask task = new ScheduledTask(command, initialDelayMillis, delayMillis, false);
    queue.add(task);
    return task;
  }

  private class WorkerThread extends Thread {
    private final DelayQueue<ScheduledTask> queue;

    WorkerThread(DelayQueue<ScheduledTask> queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      while (!isInterrupted()) {
        try {
          ScheduledTask task = queue.take();
          if (task.isCancelled()) {
            continue;
          }

          try {
            task.run();
          } catch (Throwable t) {
            t.printStackTrace();
          }

          if (task.isPeriodic() && !task.isCancelled()) {
            long newScheduledTime;
            long period = task.getPeriod();
            if (task.isFixedRate()) {
              newScheduledTime = task.getScheduledTime() + period;
            } else {
              newScheduledTime = System.currentTimeMillis() + period;
            }
            task.setScheduledTime(newScheduledTime);
            queue.add(task);
          }
        } catch (InterruptedException e) {
          interrupt();
          break;
        }
      }
    }
  }

  private static class ScheduledTask implements RunnableScheduledFuture<Void> {
    private final Runnable runnable;
    private volatile long scheduledTime;
    private final long period;
    private final boolean isFixedRate;
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);
    private final AtomicBoolean isDone = new AtomicBoolean(false);

    ScheduledTask(Runnable runnable, long delayMillis, long periodMillis, boolean isFixedRate) {
      this.runnable = runnable;
      this.scheduledTime = System.currentTimeMillis() + delayMillis;
      this.period = periodMillis;
      this.isFixedRate = isFixedRate;
    }

    @Override
    public long getDelay(TimeUnit unit) {
      long remaining = scheduledTime - System.currentTimeMillis();
      return unit.convert(remaining, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
      long thisDelay = this.getDelay(TimeUnit.MILLISECONDS);
      long otherDelay = other.getDelay(TimeUnit.MILLISECONDS);
      return Long.compare(thisDelay, otherDelay);
    }

    @Override
    public void run() {
      if (!isCancelled()) {
        try {
          runnable.run();
        } finally {
          if (!isPeriodic()) {
            isDone.set(true);
          }
        }
      }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      isCancelled.set(true);
      return true;
    }

    @Override
    public boolean isCancelled() {
      return isCancelled.get();
    }

    @Override
    public boolean isDone() {
      return isDone.get() || isCancelled();
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isPeriodic() {
      return period != 0;
    }

    public long getPeriod() {
      return period;
    }

    public boolean isFixedRate() {
      return isFixedRate;
    }

    public long getScheduledTime() {
      return scheduledTime;
    }

    public void setScheduledTime(long scheduledTime) {
      this.scheduledTime = scheduledTime;
    }
  }


  public static void main(String[] args) throws InterruptedException {
    ScheduledThreadPool executor = new ScheduledThreadPool(3);

    // One-time task with 2-second delay
    executor.schedule(
        () -> log("One-time task executed"),
        2, TimeUnit.SECONDS
    );

    // Fixed-rate task (runs every 1 second after initial 1-second delay)
    executor.scheduleAtFixedRate(
        () -> log("Fixed-Rate Task executed"),
        1, 1, TimeUnit.SECONDS
    );

    // Fixed-delay task (runs with 2-second delay between completions)
    executor.scheduleWithFixedDelay(
        () -> {
          log("Fixed-Delay Task started");
          try {
            Thread.sleep(500); // Simulate task execution time
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          log("Fixed-Delay Task completed");
        },
        1, 2, TimeUnit.SECONDS
    );

    // Let tasks run for 10 seconds
    Thread.sleep(10000);

    // Shutdown executor
    log("Shutting down executor");
    executor.shutdown();
  }

  private static void log(String message) {
    long timestamp = System.currentTimeMillis();
    String time = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date(timestamp));
    System.out.println(time + " [" + Thread.currentThread().getName() + "] " + message);
  }
}