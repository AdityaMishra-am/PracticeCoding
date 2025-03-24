package ProducerConsumerProblem;

import java.util.concurrent.atomic.AtomicLong;

public class MyStampedLock {
  // State variable: bit 0 for write lock, bits 1-15 for read count, bits 16-63 for version
  private final AtomicLong state = new AtomicLong(0L);

  // Constants for bit manipulation
  private static final long WRITE_LOCKED = 1L;          // Bit 0: write lock flag
  private static final long READER_UNIT = 2L;          // Each reader adds 2 (shifts into bits 1-15)
  private static final long VERSION_SHIFT = 16;        // Version starts at bit 16

  /**
   * Acquires the write lock, returning a stamp for unlocking.
   * Blocks until no write lock or readers are present.
   */
  public long writeLock() {
    long s, next = 0;
    do {
      s = state.get();
      // Check if write lock is held or readers are present
      if ((s & WRITE_LOCKED) != 0 || ((s >> 1) & 0x7FFF) != 0) {
        Thread.yield(); // Busy-wait; in practice, use LockSupport.park()
        continue;
      }
      // Increment version and set write lock bit
      next = (s + (1L << VERSION_SHIFT)) | WRITE_LOCKED;
    } while (!state.compareAndSet(s, next));
    return next; // Return the stamp
  }

  /**
   * Releases the write lock using the provided stamp.
   * Throws an exception if the stamp doesn't match or the lock isn't held.
   */
  public void unlockWrite(long stamp) {
    long s = state.get();
    if (s != stamp || (s & WRITE_LOCKED) == 0) {
      throw new IllegalMonitorStateException("Stamp does not match or lock not held");
    }
    // Clear the write lock bit
    state.set(s & ~WRITE_LOCKED);
  }

  /**
   * Acquires a read lock, returning a stamp for unlocking.
   * Blocks until no write lock is held.
   */
  public long readLock() {
    long s, next = 0;
    do {
      s = state.get();
      // Check if write lock is held
      if ((s & WRITE_LOCKED) != 0) {
        Thread.yield(); // Busy-wait
        continue;
      }
      // Increment read count
      next = s + READER_UNIT;
    } while (!state.compareAndSet(s, next));
    return next; // Return the stamp
  }

  /**
   * Releases a read lock using the provided stamp.
   * Throws an exception if no read locks are held.
   */
  public void unlockRead(long stamp) {
    long s, next;
    do {
      s = state.get();
      // Check if no readers or write lock is held
      if ((s & WRITE_LOCKED) != 0 || ((s >> 1) & 0x7FFF) == 0) {
        throw new IllegalMonitorStateException("Lock not held by readers");
      }
      // Decrement read count
      next = s - READER_UNIT;
    } while (!state.compareAndSet(s, next));
  }

  /**
   * Attempts an optimistic read, returning a stamp if no write lock is held,
   * or 0 if the write lock is held.
   */
  public long tryOptimisticRead() {
    long s = state.get();
    return (s & WRITE_LOCKED) == 0 ? s : 0L;
  }

  /**
   * Validates a stamp from an optimistic read.
   * Returns true if no write has occurred since the stamp was obtained.
   */
  public boolean validate(long stamp) {
    long s = state.get();
    // Check if version matches and no write lock is held
    return (s >> VERSION_SHIFT) == (stamp >> VERSION_SHIFT) && (s & WRITE_LOCKED) == 0;
  }
}
