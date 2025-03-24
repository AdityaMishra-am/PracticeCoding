package ProducerConsumerProblem;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Main {
  public static void main(String arg[]) {
    ConsumerProducer consumerProducer = new ConsumerProducer(20);
   Thread t1 = new Thread(() -> {
     for (int i = 0; i<45; i++) {
       consumerProducer.producer(i);
     }
   });
//   Thread t2 = new Thread(() -> {
//     for (int i = 0; i<55; i++) {
//       System.out.println(consumerProducer.consumer() + " t2");
//     }
//   });
//    Thread t3 = new Thread(() -> {
//      for (int i = 0; i<30; i++) {
//        System.out.println(consumerProducer.consumer()+ " t3");
//      }
//    });
    Object obj = new Object();
    PlayingWithSyncronised playingWithSyncronised = new PlayingWithSyncronised(obj);
    PlayingWithSyncronised playingWithSyncronised2 = new PlayingWithSyncronised(obj);
    PlayingWithSyncronised.setCount(0);
    Thread t2 = new Thread(() -> {
      for (int i = 0; i<100000; i++) {
        playingWithSyncronised.incrementCount();
      }
    }, "T2");
    Thread t3 = new Thread(() -> {
      for (int i = 0; i<20000; i++) {
        playingWithSyncronised2.incrementCount();
      }
    }, "T3");

    Thread t4 = new Thread(() -> {
      for (int i = 0; i<10000; i++) {
        playingWithSyncronised2.incrementCount();
      }
    }, "T4");

    ThreadPoolExecutorImplementation threadPoolExecutorImplementation = new ThreadPoolExecutorImplementation(4, 1000);
    for (int i = 0; i<1500; i++) {
      int finalI = i;
      try {
        threadPoolExecutorImplementation.submit(() -> {
        System.out.println("Task " + finalI);
        try {
        Thread.sleep(100); }
        catch (InterruptedException e) {
          //
           }
        System.out.println("Task " + finalI + " done");
      });
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }}
    try {
      Thread.sleep(2000); }
    catch (InterruptedException e) {
      //
    }
    threadPoolExecutorImplementation.shutdown();
    try {
      threadPoolExecutorImplementation.awaitTermination();
    } catch (InterruptedException e) {//
    }

   //t1.start();
    //t1.interrupt();
//   t2.start();
//   t3.start();
//   t4.start();
  }
}
