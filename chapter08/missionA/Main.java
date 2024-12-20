package chapter08.missionA;

public class Main {

    private static final int ATM_NUMBER = 1000;

    public static void main(String[] args) {
        BankAccount account;

        // Test UnsyncedBankAccount
        account = new UnsyncedBankAccount();
        testAtms(account);

        System.out.println("Balance of unsynced account after concurrent transactions:");
        System.out.printf("Actual: %d%nExpected: 0%n", account.getBalance());

        // Test SyncedBankAccount
        account = new SyncedBankAccount();
        testAtms(account);

        System.out.println("Balance of synced account after concurrent transactions:");
        System.out.printf("Actual: %d%nExpected: 0%n", account.getBalance());
    }

    private static void testAtms(BankAccount account) {
        ATM[] atms = new ATM[ATM_NUMBER];
        for (int i = 0; i < ATM_NUMBER; i++) {
            ATM atm = new ATM(account);
            atms[i] = atm;
            atm.start();
        }

        for (int i = 0; i < ATM_NUMBER; i++) {
            try {
                atms[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
