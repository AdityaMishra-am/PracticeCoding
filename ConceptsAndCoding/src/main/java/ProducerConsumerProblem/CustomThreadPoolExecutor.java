package ProducerConsumerProblem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolExecutor {
  private volatile boolean isShutdown = false;
  private final ArrayBlockingQueue<Runnable> taskQueue;
  private final Set<WorkerThread> workers = Collections.synchronizedSet(new HashSet<>());

  private final int corePoolSize;
  private final int maxPoolSize;
  private final long keepAliveTime;
  private final TimeUnit timeUnit;

  public CustomThreadPoolExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, long keepAliveTime, TimeUnit timeUnit) {
    if (corePoolSize < 0 || maxPoolSize <= 0 || maxPoolSize < corePoolSize) {
      throw new IllegalArgumentException("Invalid pool sizes");
    }
    this.corePoolSize = corePoolSize;
    this.maxPoolSize = maxPoolSize;
    this.taskQueue = new ArrayBlockingQueue<>(queueCapacity);
    this.keepAliveTime = keepAliveTime;
    this.timeUnit = timeUnit;
  }

  public void submit(Runnable task) {
    if (isShutdown) {
      throw new RejectedExecutionException("ThreadPoolExecutor is shutdown");
    }

    // If we haven't reached the core pool size, create a new worker immediately.
    if (workers.size() < corePoolSize) {
      addWorker(task);
    } else {
      // Try to enqueue the task.
      if (!taskQueue.offer(task)) {
        // If the queue is full, and we haven't reached max pool size, create a new worker.
        if (workers.size() < maxPoolSize) {
          addWorker(task);
        } else {
          // Otherwise, reject the task.
          throw new RejectedExecutionException("Queue is full and max pool size reached");
        }
      }
    }
  }

  private void addWorker(Runnable firstTask) {
    WorkerThread worker = new WorkerThread(firstTask);
    workers.add(worker);
    worker.start();
  }

  public void shutdown() {
    isShutdown = true;
    // Interrupt all worker threads so that idle threads are unblocked.
    synchronized(workers) {
      for (WorkerThread worker : workers) {
        worker.interrupt();
      }
    }
  }

  public void awaitTermination() {
    // Wait for all threads to finish.
    synchronized(workers) {
      for (WorkerThread worker : workers) {
        try {
          worker.join();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private class WorkerThread extends Thread {
    private Runnable firstTask;

    public WorkerThread(Runnable firstTask) {
      this.firstTask = firstTask;
    }

    public void run() {
      Runnable task = this.firstTask;
      this.firstTask = null;
      try {
        // Process tasks: first the initial one, then poll the queue.
        while (task != null || (task = getTask()) != null) {
          task.run();
          task = null;
        }
      } finally {
        workers.remove(this);
      }
    }

    /**
     * Retrieves a task from the queue.
     * Non-core threads will poll with timeout (using keepAliveTime) so that they can exit if idle.
     * Core threads will block until a task becomes available.
     */
    private Runnable getTask() {
      try {
        // If shutdown and no tasks are waiting, exit.
        if (isShutdown && taskQueue.isEmpty()) {
          return null;
        }
        // If current thread count is above corePoolSize, poll with timeout.
        if (workers.size() > corePoolSize) {
          return taskQueue.poll(keepAliveTime, timeUnit);
        } else {
          // Otherwise, block until a task is available.
          return taskQueue.take();
        }
      } catch (InterruptedException e) {
        // On interruption, check if shutdown and queue empty.
        if (isShutdown && taskQueue.isEmpty()) {
          return null;
        }
        // Otherwise, continue to loop (or return null so the worker can check the condition again).
        return null;
      }
    }
  }

  // Example usage:
  public static void main(String[] args) {
    // Core threads: 4, Maximum threads: 8, Queue capacity: 1000, Keep alive: 1 second.
    CustomThreadPoolExecutor executor = new CustomThreadPoolExecutor(4, 8, 1000, 1, TimeUnit.SECONDS);

    // Submit 1500 tasks.
    for (int i = 0; i < 1500; i++) {
      final int taskNumber = i;
      try {
        executor.submit(() -> {
          System.out.println("Executing task " + taskNumber);
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          System.out.println("Completed task " + taskNumber);
        });
      } catch (RejectedExecutionException e) {
        System.err.println("Task " + taskNumber + " rejected: " + e.getMessage());
      }
    }

    // Allow some time for tasks to run.
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    executor.shutdown();
    executor.awaitTermination();
    System.out.println("All tasks completed, executor terminated.");
  }
}
