package co.practice;
import java.util.concurrent.*;

/*
Bounded Queue Blocking:
The submitTask() method uses taskQueue.put(task), which blocks if the queue is full until there is available space.

Worker Thread:
A dedicated worker thread continuously calls taskQueue.take() (which blocks if the queue is empty) and submits the retrieved tasks to the ThreadPoolExecutor. This decouples task submission from task execution.

ThreadPoolExecutor:
The executor is created with its own internal queue, but in this pattern, the external taskQueue controls backpressure. The internal queue of the executor isn't used for controlling task submissions since we already handle that externally.
*/
public class BlockingQueueExample {
  // A bounded blocking queue to hold tasks before they're executed.
  private final BlockingQueue<Runnable> taskQueue;
  private final ThreadPoolExecutor executor;

  public BlockingQueueExample(int corePoolSize, int maximumPoolSize, int queueCapacity) {
    // Create a bounded blocking queue with the specified capacity.
    this.taskQueue = new ArrayBlockingQueue<>(queueCapacity);
    this.executor = new ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        60L,
        TimeUnit.SECONDS,
        taskQueue
        //new LinkedBlockingQueue<>() // Executor's own internal queue (unused in this pattern)
//        (Runnable runnable) -> {
//          Thread th = new Thread(runnable);
//          th.setDaemon(false);
//          th.setPriority(Thread.NORM_PRIORITY);
//          return th;
//        }
    );
    // Start a worker thread that polls the taskQueue and submits tasks to the executor.
    try {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(()->{return  "hello";}).thenCompose((String val)->{ return CompletableFuture.supplyAsync(()->{return   val + " world";});});
    System.out.println(completableFuture.get());}
   catch (Exception e) {
     ///
    }
    Thread worker = new Thread(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        try {
          // This will block until a task becomes available.
          Runnable task = taskQueue.take();
          executor.execute(task);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });
    worker.setDaemon(true);
    worker.start();
  }

  // This method blocks if the taskQueue is full.
  public void submitTask(Runnable task) throws InterruptedException {
    taskQueue.put(task); // This call blocks until space is available in the queue.
  }

  public void shutdown() {
    executor.shutdown();
  }

  public static void main(String[] args) {
    BlockingQueueExample blockingExample = new BlockingQueueExample(2, 4, 10);

    // Producer thread simulating task submission.
    Thread producer = new Thread(() -> {
      for (int i = 0; i < 50; i++) {
        final int taskId = i;
        try {
          blockingExample.submitTask(() -> {
            System.out.println("Executing task " + taskId + " on " +
                Thread.currentThread().getName());
            try {
              Thread.sleep(2000); // Simulate task work.
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          });
          System.out.println("Submitted task " + taskId);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    });

    producer.start();

    try {
      producer.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    blockingExample.shutdown();
  }
}
