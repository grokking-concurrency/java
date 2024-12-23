package chapter09.missionA;

public class DumplingPool {

    private volatile int dumplings;

    public DumplingPool(int dumplings) {
        this.dumplings = dumplings;
    }

    public synchronized void decrease() {
        this.dumplings--;
    }

    public int count() {
        return dumplings;
    }
}
