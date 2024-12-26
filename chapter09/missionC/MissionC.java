package chapter09.missionC;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MissionC {

    private static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private static int counter = 0;

    public static void main(String[] args) {
        Thread[] threads = {
            new User(0),
            new User(1),
            new Librarian()
        };

        for (Thread thread : threads) {
            thread.start();
        }
    }

    private static class User extends Thread {

        private static final String PREFIX = "User ";

        public User(int idx) {
            super(PREFIX + idx);
        }

        @Override
        public void run() {
            while (true) {
                rwLock.readLock().lock();

                System.out.printf("%s reading: %d%n", getName(), counter);

                rwLock.readLock().unlock();

                // Simulating some real action here
                try {
                    Thread.sleep((long) (1000 + 2000 * Math.random()));
                } catch (InterruptedException e) {
                    this.interrupt();
                    System.err.println(getName() + ": interrupted");
                }
            }
        }
    }

    private static class Librarian extends Thread {

        @Override
        public void run() {
            while (true) {
                rwLock.writeLock().lock();

                System.out.println("Librarian writing...");
                counter += 1;
                System.out.println("New value : " + counter);

                rwLock.writeLock().unlock();

                // Simulating some real action here
                try {
                    Thread.sleep((long) (1000 + 2000 * Math.random()));
                } catch (InterruptedException e) {
                    this.interrupt();
                    System.err.println(getName() + ": interrupted");
                }
            }
        }
    }
}
