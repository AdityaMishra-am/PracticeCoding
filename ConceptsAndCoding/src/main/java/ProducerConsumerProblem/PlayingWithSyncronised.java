package ProducerConsumerProblem;

public class PlayingWithSyncronised {

  final Object sync;



  public static int count;

  public PlayingWithSyncronised(Object sync) {
    this.sync = sync;
  }

  public static void setCount(int count) {
    PlayingWithSyncronised.count = count;
  }
  public void incrementCount() {

      count++;
//      try {
//        Thread.sleep(50);
//      } catch (Exception e) {
//        //
//      }
      System.out.println(
          "Count incremented to " + count + " by thread " + Thread.currentThread().getName());
    }


}
