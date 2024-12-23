package chapter09.missionA;

public class Philosopher extends Thread {

    private static final long SLEEP_TIME_IN_MILLISECONDS = 100;

    private final Waiter waiter;
    private final DumplingPool dumplingPool;
    private final Chopstick leftChopstick;
    private final Chopstick rightChopstick;

    public Philosopher(String name, Waiter waiter, DumplingPool dumplingPool, Chopstick leftChopstick, Chopstick rightChopstick) {
        super(name);
        this.waiter = waiter;
        this.dumplingPool = dumplingPool;
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
    }

    @Override
    public void run() {
        while (dumplingPool.count() > 0) {
            System.out.println(getName() + " asks waiter for chopsticks");
            waiter.askForChopsticks(leftChopstick, rightChopstick, getName());

            dumplingPool.decrease();
            System.out.println(getName() + " eats a dumpling. Dumplings left: " + dumplingPool.count());

            waiter.releaseChopsticks(leftChopstick, rightChopstick, getName());

            try {
                Thread.sleep(SLEEP_TIME_IN_MILLISECONDS);
            } catch (InterruptedException e) {
                this.interrupt();
                System.err.println(getName() + ": interrupted");
            }
        }
    }
}
