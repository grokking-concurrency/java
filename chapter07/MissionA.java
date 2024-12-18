package chapter07;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MissionA {

    private record Washload(int number) {
    }

    private static class Washer extends Thread {

        private static final long WASHING_TIME = 4000L;

        private static final String THREAD_NAME = "Washer";

        private final BlockingQueue<Washload> inQueue;
        private final BlockingQueue<Washload> outQueue;

        public Washer(BlockingQueue<Washload> inQueue, BlockingQueue<Washload> outQueue) {
            super(THREAD_NAME);
            this.inQueue = inQueue;
            this.outQueue = outQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Washload washload = inQueue.take();

                    System.out.printf("%s: washing Washload #%d...%n", THREAD_NAME, washload.number());
                    Thread.sleep(WASHING_TIME);

                    outQueue.add(washload);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class Dryer extends Thread {

        private static final long DRYING_TIME = 2000L;

        private static final String THREAD_NAME = "Dryer";

        private final BlockingQueue<Washload> inQueue;
        private final BlockingQueue<Washload> outQueue;

        public Dryer(BlockingQueue<Washload> inQueue, BlockingQueue<Washload> outQueue) {
            super(THREAD_NAME);
            this.inQueue = inQueue;
            this.outQueue = outQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Washload washload = inQueue.take();

                    System.out.printf("%s: drying Washload #%d...%n", THREAD_NAME, washload.number());
                    Thread.sleep(DRYING_TIME);

                    outQueue.add(washload);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class Folder extends Thread {

        private static final String THREAD_NAME = "Folder";

        private static final long FOLDING_TIME = 2000L;

        private final BlockingQueue<Washload> inQueue;

        public Folder(BlockingQueue<Washload> inQueue) {
            super(THREAD_NAME);
            this.inQueue = inQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Washload washload = inQueue.take();

                    Thread.sleep(FOLDING_TIME);
                    System.out.printf("%s: folding Washload #%d done!%n", THREAD_NAME, washload.number());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class Pipeline {

        private static final int WASH_LOAD_COUNT = 4;

        private BlockingQueue<Washload> assembleLaundryForWashing() {
            BlockingQueue<Washload> washloads = new LinkedBlockingQueue<>(WASH_LOAD_COUNT);

            for (int number = 1; number <= WASH_LOAD_COUNT; number++) {
                washloads.add(new Washload(number));
            }

            return washloads;
        }

        private void runConcurrently() throws InterruptedException {
            BlockingQueue<Washload> toBeWashed = assembleLaundryForWashing();
            BlockingQueue<Washload> toBeDried = new LinkedBlockingQueue<>(WASH_LOAD_COUNT);
            BlockingQueue<Washload> toBeFolded = new LinkedBlockingQueue<>(WASH_LOAD_COUNT);

            Washer washer = new Washer(toBeWashed, toBeDried);
            Dryer dryer = new Dryer(toBeDried, toBeFolded);
            Folder folder = new Folder(toBeFolded);

            washer.start();
            dryer.start();
            folder.start();

            washer.join();
            dryer.join();
            folder.join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Pipeline pipeline = new Pipeline();
        pipeline.runConcurrently();
    }
}
