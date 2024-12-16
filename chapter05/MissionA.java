package chapter05;

public class MissionA {

    private static final int SIZE = 5;
    private static final int EMPTY_DATA = -1;
    private static final long SLEEP_TIME = 1000L;

    private static int[] sharedMemory;

    public static void main(String[] args) throws InterruptedException {
        sharedMemory = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            sharedMemory[i] = EMPTY_DATA;
        }

        Thread[] threads = new Thread[]{
            new Thread(MissionA::consumer, "Consumer"),
            new Thread(MissionA::producer, "Producer")
        };

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static void producer() {
        String threadName = Thread.currentThread().getName();

        for (int i = 0; i < SIZE; i++) {
            System.out.printf("%s: Writing %d\n", threadName, i);
            sharedMemory[i] = i;
        }
    }

    private static void consumer() {
        String threadName = Thread.currentThread().getName();

        for (int i = 0; i < SIZE; i++) {
            while (true) {
                if (sharedMemory[i] == EMPTY_DATA) {
                    System.out.printf("%s: Data not available\n", threadName);

                    try {
                        System.out.println("Sleeping for 1 second before trying");
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                System.out.printf("%s: Read: %d\n", threadName, sharedMemory[i]);
                break;
            }
        }
    }
}
