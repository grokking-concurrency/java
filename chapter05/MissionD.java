package chapter05;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MissionD {

    private static final int NUM_THREADS = 5;
    private static final int TERMINATION_WAITING_TIME = 20;

    private static final long SLEEP_TIME = 3000L;

    public static void main(String[] args) {
        try (ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS)) {
            for (int i = 0; i < 20; i++) {
                int taskNumber = i + 1;
                threadPool.submit(() -> {
                    String threadName = Thread.currentThread().getName();
                    System.out.printf("%s doing %d work%n", threadName, taskNumber);

                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        System.err.printf("%s: Task %d failed%n", threadName, taskNumber);
                        Thread.currentThread().interrupt();
                    }
                });
            }

            System.out.println("All work requests sent");

            threadPool.shutdown();

            try {
                if (threadPool.awaitTermination(TERMINATION_WAITING_TIME, TimeUnit.SECONDS)) {
                    System.out.println("All works completed");
                } else {
                    System.err.println("Some works failed");
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                System.err.printf("awaitTermination failed : %s%n", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
