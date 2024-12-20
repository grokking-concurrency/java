package chapter08.missionA;

public class UnsyncedBankAccount extends BankAccount {

    @Override
    public void deposit(long amount) {
        if (amount > 0) {
            super.addBalance(amount);
        } else {
            throw new IllegalArgumentException("You can't deposit a negative amount fo money");
        }
    }

    @Override
    public void withdraw(long amount) {
        if (super.isBalanceZero() || super.getBalance() < amount) {
            throw new IllegalArgumentException("Account does not have sufficient funds");
        } else {
            super.removeBalance(amount);
        }
    }
}
