package chatper04;

public class MissionA {

	private static final String HEADER = "- ";
	private static final String THREAD_NAME = "custom-";
	private static final String LINE_BREAKER = "-".repeat(10);

	private static final int NUM_THREADS = 5;
	private static final int SLEEP_TIME = 3000;

	public static void main(String[] args) throws InterruptedException {
		displayThreads();

		System.out.printf("Starting %d CPU wasters...\n", NUM_THREADS);

		for (int i = 0; i < NUM_THREADS; i++) {
			Thread thread = new Thread(MissionA::cpuWaster, THREAD_NAME + i);
			thread.start();
		}
		Thread.sleep(100);

		displayThreads();
	}

	private static void displayThreads() {
		System.out.println(LINE_BREAKER);
		System.out.println("Current process PID: " + ProcessHandle.current().pid());
		System.out.println("Thread Count: " + Thread.activeCount());
		System.out.println("Active threads: ");

		Thread[] threads = new Thread[Thread.activeCount()];
		Thread.enumerate(threads);

		for (Thread thread : threads) {
			if (thread != null) {
				System.out.print(HEADER);
				System.out.println(thread);
			}
		}

		System.out.println(LINE_BREAKER);
	}

	private static void cpuWaster() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " started");
		try {
			Thread.sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
