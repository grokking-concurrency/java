package chapter08.missionB;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class MissionB {

    public static void main(String[] args) {
        int numberOfCars = 10;
        Garage garage = new Garage();
        testGarage(garage, numberOfCars);

        System.out.println("Number of parked cars after a busy day:");
        System.out.printf("Actual: %d%nExpected: 0", garage.countParkedCars());
    }

    private static void testGarage(Garage garage, int numberOfCars) {
        Thread[] threads = new Thread[numberOfCars];
        for (int i = 0; i < numberOfCars; i++) {
            int carNumber = i + 1;
            Thread thread = new Thread(() -> parkCar(garage, "Car #" + carNumber));
            threads[i] = thread;
            thread.start();
        }

        for (int i = 0; i < numberOfCars; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void parkCar(Garage garage, String carName) {
        try {
            garage.enter(carName);
            Thread.sleep((long) (1000 + Math.random() * 1000));
            garage.exit(carName);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Garage {

        private static final int TOTAL_SPOTS = 3;

        private final Semaphore semaphore;
        private final List<String> parkedCars;
        private final ReentrantLock carsLock;

        public Garage() {
            semaphore = new Semaphore(TOTAL_SPOTS);
            parkedCars = new ArrayList<>();
            carsLock = new ReentrantLock();
        }

        public int countParkedCars() {
            return parkedCars.size();
        }

        public void enter(String carName) {
            try {
                semaphore.acquire();
                carsLock.lock();
                parkedCars.add(carName);
                System.out.println(carName + " parked");
                carsLock.unlock();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void exit(String carName) {
            carsLock.lock();
            parkedCars.remove(carName);
            System.out.println(carName + " leaving");
            semaphore.release();
            carsLock.unlock();
        }
    }
}
