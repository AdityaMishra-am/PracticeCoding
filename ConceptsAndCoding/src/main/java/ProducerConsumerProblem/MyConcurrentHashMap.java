package ProducerConsumerProblem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MyConcurrentHashMap<K,V>{

  Segment[] segments;

  public MyConcurrentHashMap() {
    this.segments = new Segment[16];
    for (int i = 0; i<16; i++) {
      segments[i] = new Segment<>();
    }
  }
  private int getSegmentIndex(Object key) {
    int hash = key.hashCode();
    return (hash & 0x7FFFFFFF) % segments.length;
  }
  public void put(K key, V value) {
    int idx = getSegmentIndex(key);
    segments[idx].put(key,value);
  }
  public V get(K key) {
    int idx = getSegmentIndex(key);
    return (V) segments[idx].get(key);
  }
public void remove(K key) {
  int idx = getSegmentIndex(key);
  segments[idx].remove(key);
}

  private static class Segment<K,V> {
    private final MyReentrantLock reentrantLock = new MyReentrantLock();
    private final Map<K,V> map = new HashMap<>();

    public V get(K k) {
      try {
        reentrantLock.lock();
        if (map.containsKey(k)) {
          return map.get(k);
        }
        return null;
      } finally {
        reentrantLock.unlock();
      }
    }
    public void put(K key, V value) {
      try {
        reentrantLock.lock();
        map.put(key,value);
      } finally {
        reentrantLock.unlock();
      }
    }

    public void remove(K k) {
      try {
        reentrantLock.lock();
        map.remove(k);
      } finally {
        reentrantLock.unlock();
      }
    }

  }
  public static void main(String[] args) {
    MyConcurrentHashMap<String, Integer> map = new MyConcurrentHashMap<>();
    map.put("one", 1);
    map.put("two", 2);

    System.out.println("Value for 'one': " + map.get("one"));
    System.out.println("Value for 'two': " + map.get("two"));
    map.remove("one");
    System.out.println("After removal, value for 'one': " + map.get("one"));
  }

}
