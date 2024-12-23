package chapter09.missionA;

public class Main {

    private static final int NUM_DUMPLINGS = 20;

    public static void main(String[] args) {
        Chopstick chopstickA = new Chopstick("chopstick_a");
        Chopstick chopstickB = new Chopstick("chopstick_b");

        Waiter waiter = new Waiter();
        DumplingPool dumplingPool = new DumplingPool(NUM_DUMPLINGS);

        Philosopher philosopher1 = new Philosopher("Philosopher #1", waiter, dumplingPool, chopstickA, chopstickB);
        Philosopher philosopher2 = new Philosopher("Philosopher #2", waiter, dumplingPool, chopstickB, chopstickA);

        philosopher1.start();
        philosopher2.start();
    }
}
