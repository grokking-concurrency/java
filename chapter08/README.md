# Mission A: Mutex
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9
import sys
import time
import typing as T

from abc import ABC, abstractmethod
from threading import Lock
from threading import Thread

""" Abstract bank account. """
class BankAccount(ABC):
    """ Abstract Base Class for bank accounts"""

    balance: float

    def __init__(self, balance: float = 0):
        self.balance: float = balance

    @abstractmethod
    def deposit(self, amount: float) -> None:
        ...

    @abstractmethod
    def withdraw(self, amount: float) -> None:
        ...

"""Bank account without synchronization cause race condition """

class UnsyncedBankAccount(BankAccount):
    """Bank account without synchronization"""

    def deposit(self, amount: float) -> None:
        if amount > 0:
            self.balance += amount
        else:
            raise ValueError("You can't deposit a negative amount of money")

    def withdraw(self, amount: float) -> None:
        if 0 < amount <= self.balance:
            self.balance -= amount
        else:
            raise ValueError("Account does not have sufficient funds")
            
class SyncedBankAccount(UnsyncedBankAccount):
    """Bank account with synchronization strategy, thread-safe"""

    def __init__(self, balance: float = 0):
        super().__init__(balance)
        self.mutex = Lock()

    def deposit(self, amount: float) -> None:
        # acquiring a lock on the shared resource
        self.mutex.acquire()
        super().deposit(amount)
        self.mutex.release()

    def withdraw(self, amount: float) -> None:
        self.mutex.acquire()
        super().withdraw(amount)
        self.mutex.release()

"""Bank account without synchronization cause race condition """

THREAD_DELAY = 1e-16

class ATM(Thread):
    """Automated teller machine (ATM) or cash machine"""

    def __init__(self, bank_account: BankAccount):
        super().__init__()
        self.bank_account = bank_account

    def transaction(self) -> None:
        self.bank_account.deposit(10)
        # simulating some real action here
        time.sleep(0.001)
        self.bank_account.withdraw(10)

    def run(self) -> None:
        self.transaction()


def test_atms(account: BankAccount, atm_number: int = 1000) -> None:
    atms: T.List[ATM] = []
    # create `atm_number` threads that will deposit and withdraw money
    # from account concurrently
    for _ in range(atm_number):
        atm = ATM(account)
        atms.append(atm)
        atm.start()

    # waiting for atm threads to finish the execution
    for atm in atms:
        atm.join()


if __name__ == "__main__":
    atm_number = 1000
    # greatly improve the chance of an operation being interrupted
    # by thread switch, thus testing synchronization effectively.
    sys.setswitchinterval(THREAD_DELAY)

    # test unsynced bank account
    account = UnsyncedBankAccount()
    test_atms(account, atm_number=atm_number)

    print("Balance of unsynced account after concurrent transactions:")
    print(f"Actual: {account.balance}\nExpected: 0")

    # test synced bank account
    account = SyncedBankAccount()
    test_atms(account, atm_number=atm_number)

    print("Balance of synced account after concurrent transactions:")
    print(f"Actual: {account.balance}\nExpected: 0")
```

### Sample Output
```
Balance of unsynced account after concurrent transactions:
Actual: 400
Expected: 0
Balance of synced account after concurrent transactions:
Actual: 0
Expected: 0
```

# Mission B: Semaphore
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Implementing parking garage using semaphore to control critical section"""

import typing as T
import time
import random
from threading import Thread, Semaphore, Lock

TOTAL_SPOTS = 3


class Garage:

    def __init__(self) -> None:
        self.semaphore = Semaphore(TOTAL_SPOTS)
        self.cars_lock = Lock()
        self.parked_cars: T.List[str] = []

    def count_parked_cars(self) -> int:
        return len(self.parked_cars)

    def enter(self, car_name: str) -> None:
        """Enter the garage"""
        self.semaphore.acquire()
        self.cars_lock.acquire()
        self.parked_cars.append(car_name)
        print(f"{car_name} parked")
        self.cars_lock.release()

    def exit(self, car_name: str) -> None:
        """Car exits the garage"""
        self.cars_lock.acquire()
        self.parked_cars.remove(car_name)
        print(f"{car_name} leaving")
        self.semaphore.release()
        self.cars_lock.release()


def park_car(garage: Garage, car_name: str) -> None:
    """Emulate parked car behavior"""
    garage.enter(car_name)
    time.sleep(random.uniform(1, 2))
    garage.exit(car_name)


def test_garage(garage: Garage, number_of_cars: int = 10) -> None:
    threads = []
    for car_num in range(number_of_cars):
        t = Thread(target=park_car,
                   args=(garage, f"Car #{car_num}"))
        threads.append(t)
        t.start()

    for thread in threads:
        thread.join()


if __name__ == "__main__":
    number_of_cars = 10
    garage = Garage()
    # test garage by concurrently arriving cars
    test_garage(garage, number_of_cars)

    print("Number of parked cars after a busy day:")
    print(f"Actual: {garage.count_parked_cars()}\nExpected: 0")
```

### Sample Output
```
Car #0 parked
Car #1 parked
Car #2 parked
Car #2 leaving
Car #3 parked
Car #0 leaving
Car #4 parked
Car #1 leaving
Car #5 parked
Car #3 leaving
Car #6 parked
Car #4 leaving
Car #7 parked
Car #5 leaving
Car #8 parked
Car #6 leaving
Car #9 parked
Car #7 leaving
Car #9 leaving
Car #8 leaving
Number of parked cars after a busy day:
Actual: 0
Expected: 0
```
