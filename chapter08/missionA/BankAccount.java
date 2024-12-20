package chapter08.missionA;

public abstract class BankAccount {

    private static final long ZERO_BALANCE = 0L;

    private long balance;

    public BankAccount() {
        this.balance = 0L;
    }

    public abstract void deposit(long amount);

    public abstract void withdraw(long amount);

    public boolean isBalanceZero() {
        return this.balance == ZERO_BALANCE;
    }

    public void addBalance(long amount) {
        this.balance += amount;
    }

    public void removeBalance(long amount) {
        this.balance -= amount;
    }

    public long getBalance() {
        return this.balance;
    }
}
