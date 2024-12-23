package chapter09.missionA;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chopstick {

    private final Lock lock;
    private final String name;

    public Chopstick(String name) {
        this.name = name;
        lock = new ReentrantLock();
    }

    public void acquire() {
        lock.lock();
    }

    public void release() {
        lock.unlock();
    }

    public String getName() {
        return this.name;
    }
}
