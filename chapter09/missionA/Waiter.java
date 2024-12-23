package chapter09.missionA;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Waiter extends Thread {

    private final Lock lock;

    public Waiter() {
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        super.run();
    }

    public void askForChopsticks(Chopstick leftChopstick, Chopstick rightChopstick, String philosopherName) {
        lock.lock();
        leftChopstick.acquire();
        System.out.println(philosopherName + " grabbed " + leftChopstick.getName());
        rightChopstick.acquire();
        System.out.println(philosopherName + " grabbed " + rightChopstick.getName());
        lock.unlock();
    }

    public void releaseChopsticks(Chopstick leftChopstick, Chopstick rightChopstick, String philosopherName) {
        System.out.println(philosopherName + " released " + rightChopstick.getName());
        rightChopstick.release();
        System.out.println(philosopherName + " released " + leftChopstick.getName());
        leftChopstick.release();
        System.out.println();
    }
}
