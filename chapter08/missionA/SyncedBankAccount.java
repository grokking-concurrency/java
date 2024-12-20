package chapter08.missionA;

import java.util.concurrent.locks.ReentrantLock;

public class SyncedBankAccount extends UnsyncedBankAccount {

    private final ReentrantLock mutex = new ReentrantLock();

    @Override
    public void deposit(long amount) {
        mutex.lock();
        super.deposit(amount);
        mutex.unlock();
    }

    @Override
    public void withdraw(long amount) {
        mutex.lock();
        super.withdraw(amount);
        mutex.unlock();
    }
}
