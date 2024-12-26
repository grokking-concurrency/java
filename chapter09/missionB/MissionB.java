package chapter09.missionB;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MissionB {

    /* Print format */
    public static final String FONT_BLACK = "\u001B[30m";
    private static final String FONT_GREEN = "\u001B[32m";

    private static final int BUFFER_SIZE = 5;
    private static final String[] BUFFER = new String[BUFFER_SIZE];
    private static final Semaphore empty = new Semaphore(BUFFER_SIZE);
    private static final Semaphore full = new Semaphore(0);
    private static final Lock mutex = new ReentrantLock();

    public static void main(String[] args) {
        int itemsPerProducer = 5;

        Thread[] threads = {
            new Producer("SpongeBob", itemsPerProducer),
            new Producer("Patrick", itemsPerProducer),
            new Consumer("Squidward", 2 * itemsPerProducer)
        };

        for(Thread thread : threads) {
            thread.start();
        }
    }

    private static class Producer extends Thread {

        private static final long SLEEP_TIME_IN_MILLIS = 1000L;

        private static int producerIdx = 0;

        private final int maximumItems;
        private int counter;

        public Producer(String name, int maximumItems) {
            super(name);
            this.maximumItems = maximumItems;
        }

        private int nextIndex(int index) {
            return (index + 1) % BUFFER_SIZE;
        }

        @Override
        public void run() {
            while (this.counter < maximumItems) {
                try {
                    empty.acquire();
                    mutex.lock();

                    this.counter++;
                    String data = String.format("%s-%d", getName(), this.counter);
                    BUFFER[producerIdx] = data;
                    System.out.printf(FONT_BLACK + "%s produced: '%s' into slot %d%n", getName(), data, producerIdx);
                    producerIdx = this.nextIndex(producerIdx);

                    mutex.unlock();
                    full.release();

                    Thread.sleep(SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    this.interrupt();
                    System.err.println(getName() + ": interrupted");
                }
            }
        }
    }

    private static class Consumer extends Thread {

        private static final long SLEEP_TIME_IN_MILLIS = 2000L;

        private int idx = 0;
        private int counter = 0;
        private final int totalItems;

        public Consumer(String name, int totalItems) {
            super(name);
            this.totalItems = totalItems;
        }

        @Override
        public void run() {
            while (counter < totalItems) {
                try {
                    full.acquire();
                    mutex.lock();

                    String item = BUFFER[idx];
                    System.out.printf(FONT_GREEN + "%s consumed item: '%s' from slot %d%n", getName(), item, idx);
                    idx = (idx + 1) % BUFFER_SIZE;
                    counter++;

                    mutex.unlock();
                    empty.release();

                    Thread.sleep(SLEEP_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    this.interrupt();
                    System.err.println(getName() + ": interrupted");
                }
            }
        }
    }
}
