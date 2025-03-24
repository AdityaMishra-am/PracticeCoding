package CustomThreadPoolExecutor;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPool  {
  List<Thread> threadList;
  LinkedBlockingQueue queue;
  ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 10, 1000, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
  ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
  void func() {
    threadPoolExecutor.shutdownNow();
  }

}
