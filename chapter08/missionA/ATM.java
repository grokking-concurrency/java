package chapter08.missionA;

public class ATM extends Thread {

    private static final long DEPOSIT_MONEY = 10L;
    private static final long WITHDRAW_MONEY = 10L;
    private static final long SLEEP_TIME = 1L;

    private final BankAccount bankAccount;

    public ATM(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    private void transaction() {
        this.bankAccount.deposit(DEPOSIT_MONEY);

        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("ATM interrupted");
        }

        this.bankAccount.withdraw(WITHDRAW_MONEY);
    }

    @Override
    public void run() {
        transaction();
    }
}
