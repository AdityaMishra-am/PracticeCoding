package ProducerConsumerProblem;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorImplementation {
  volatile boolean isShutdown = false;
  private final WorkerThread[] workerThreads;
  private final MyArrayBlockingQueue<Runnable> taskQueue;

  int size;
  public ThreadPoolExecutorImplementation (int poolSize, int cap) {
    this.size = cap;
    this.workerThreads = new WorkerThread[poolSize];
    this.taskQueue = new MyArrayBlockingQueue<>(cap);
    for (int i = 0; i < poolSize; i++) {
      workerThreads[i] = new WorkerThread("thread"+i, taskQueue);
      workerThreads[i].start();
    }
  }
//  public synchronized void submit(Runnable task) {
//    if (isShutdown) {
//      throw new RejectedExecutionException("ThreadPoolExecutor is shutdown");
//    }
//    if (!taskQueue.offer(task)) {
//      throw new RejectedExecutionException("Queue is full");
//    }
//
//  }
public <T> Future<T> submit(Callable<T> task) throws RejectedExecutionException {
  if (isShutdown) {
    throw new RejectedExecutionException("Thread pool is shutdown");
  }
  FutureTask<T> futureTask = new FutureTask<>(task);
  taskQueue.offer(futureTask);
  return futureTask;
}

  /**
   * Submits a Runnable task for execution, returning a Future that yields null upon completion.
   */
  public Future<?> submit(Runnable task) throws RejectedExecutionException {
    return submit(task, null);
  }

  public <T> Future<T> submit(Runnable task, T result) throws RejectedExecutionException {
    if (isShutdown) {
      throw new RejectedExecutionException("Thread pool is shutdown");
    }
    FutureTask<T> futureTask = new FutureTask<>(task, result);
    taskQueue.offer(futureTask);
    return futureTask;
  }

  public void awaitTermination() throws InterruptedException {
    System.out.println("Waiting for shutdown");
    for (WorkerThread worker : workerThreads) {
      worker.join();
    }
    System.out.println("Completing shutdown");
  }
  void shutdown() {
    System.out.println("calling shutdown");
    this.isShutdown = true;
    for (WorkerThread worker : workerThreads ){
      worker.interrupt();
    }
  }

  private class WorkerThread extends Thread {

    MyArrayBlockingQueue<Runnable> taskQueue;
    public WorkerThread(String name, MyArrayBlockingQueue<Runnable> taskQueue) {
      super(name);
      this.taskQueue = taskQueue;
    }
    public void run() {
      try {
        while (true) {
          //Runnable task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
          Runnable task = taskQueue.poll();
          if (task != null) {
            task.run();
          }
          if (isShutdown && taskQueue.size()==0) {
            break;
          }
        }
      } catch (InterruptedException e) {
        while (isShutdown && taskQueue.size()!=0) {
          Runnable task = null;
          try {
            task = taskQueue.poll();
          } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
          }
          if (task != null) {
            task.run();
          }
        }
      }
    }
  }
}



