package co.practice;

import static java.lang.Thread.sleep;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
  static Queue<String> q = new ConcurrentLinkedQueue<>();
  static final int CAP = 5;
  public static void main(String[] args) {
    System.out.println("Hello world!");
    SharedResource sharedResource1 = new SharedResource(5);
    SharedResource sharedResource2 = new SharedResource(5);

    Thread t1 = new Thread(() -> {
      for(int i = 0 ; i<200; i++)
        sharedResource1.inc();
    });
    Thread t2 = new Thread(() -> {

      for(int i = 0 ; i<200; i++)
        sharedResource2.inc();
    });
    Thread t3 = new Thread(() -> {
      for(int i = 0 ; i<600; i++)
        sharedResource1.inc();
    });
    ReadWriteLock lock = new ReentrantReadWriteLock();
//    Thread t1 = new Thread(() -> {
//      System.out.println("==>>>"+sharedResource1.read(lock));
//    });
//    Thread t2 = new Thread(() -> {
//      System.out.println("==>>>"+sharedResource2.read(lock));
//    });
//    Thread t3 = new Thread(() -> {
//      System.out.println("==>>>"+sharedResource2.write(lock, 90));
//    });
//    Thread t1 = new Thread(() -> {
//      try {
//        sleep(5000);
//      } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//      }
//      for (int i = 0; i<10; i++) {
//        try {
//          sharedResource.produce(i);
//        } catch (InterruptedException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    });
//    Thread t2 = new Thread(() -> {
//      for (int i = 0; i<10; i++) {
//        try {
//          sharedResource.consume();
//        } catch (InterruptedException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    });
//    t1.setDaemon(true);
    t1.start();
    t2.start();
    t3.start();
    System.out.println("Finishing main");
  }

}