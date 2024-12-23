# Mission A: Dining Philosophers Problem - Arbitrator Model
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Philosophers thinking and eating dumplings - avoiding deadlock by introducing arbitrary"""

import time
from threading import Thread, Lock
from threading import Lock
from typing import Any, Tuple

dumplings = 20

"""Don"t mind it - just upgrading a mutex
     with additional attribute
     to make examples more explicit."""

class LockWithName:
    """ A standard python lock but with name attribute added. """

    def __init__(self, name: str):
        self.name = name
        self._lock = Lock()

    def acquire(self) -> None:
        self._lock.acquire()

    def release(self) -> None:
        self._lock.release()

    def locked(self) -> bool:
        return self._lock.locked()

    def __enter__(self) -> None:
        """ Allows this to be used with context management. """
        self.acquire()

    def __exit__(self, *args: Tuple[Any]) -> None:
        """ Allows this to be used with context management. """
        self.release()


class Waiter:
    def __init__(self) -> None:
        self.mutex = Lock()

    def ask_for_chopsticks(self, left_chopstick: LockWithName,
                           right_chopstick: LockWithName) -> None:
        with self.mutex:
            left_chopstick.acquire()
            print(f"{left_chopstick.name} grabbed")
            right_chopstick.acquire()
            print(f"{right_chopstick.name} grabbed")

    def release_chopsticks(self, left_chopstick: LockWithName,
                           right_chopstick: LockWithName) -> None:
        right_chopstick.release()
        print(f"{right_chopstick.name} released")
        left_chopstick.release()
        print(f"{left_chopstick.name} released\n")


class Philosopher(Thread):
    def __init__(self, name: str, waiter: Waiter,
                 left_chopstick: LockWithName,
                 right_chopstick: LockWithName):
        super().__init__()
        self.name = name
        self.left_chopstick = left_chopstick
        self.right_chopstick = right_chopstick
        self.waiter = waiter

    def run(self) -> None:
        # using globally shared variable
        global dumplings

        while dumplings > 0:
            print(f"{self.name} asks waiter for chopsticks")
            self.waiter.ask_for_chopsticks(
                self.left_chopstick, self.right_chopstick)

            dumplings -= 1
            print(f"{self.name} eats a dumpling. "
                  f"Dumplings left: {dumplings}")
            print(f"{self.name} returns chopsticks to waiter")
            self.waiter.release_chopsticks(
                self.left_chopstick, self.right_chopstick)
            time.sleep(0.1)


if __name__ == "__main__":
    chopstick_a = LockWithName("chopstick_a")
    chopstick_b = LockWithName("chopstick_b")

    waiter = Waiter()
    philosopher_1 = Philosopher("Philosopher #1", waiter, chopstick_a,
                                chopstick_b)
    philosopher_2 = Philosopher("Philosopher #2", waiter, chopstick_b,
                                chopstick_a)

    philosopher_1.start()
    philosopher_2.start()
```

### Sample Output
```
Philosopher #1 asks waiter for chopsticks
chopstick_a grabbed
chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 19
Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_a released

Philosopher #2 asks waiter for chopsticks
chopstick_b grabbed
chopstick_a grabbed
Philosopher #2 eats a dumpling. Dumplings left: 18
Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_b released

Philosopher #1 asks waiter for chopsticks
Philosopher #2 asks waiter for chopsticks
chopstick_b grabbed
chopstick_a grabbed
Philosopher #2 eats a dumpling. Dumplings left: 17

Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_a grabbed
chopstick_b released
chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 16

Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_a released

Philosopher #1 asks waiter for chopsticks
Philosopher #2 asks waiter for chopsticks
chopstick_b grabbed

chopstick_a grabbed
Philosopher #2 eats a dumpling. Dumplings left: 15
Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_a grabbed
chopstick_b released
chopstick_b grabbed

Philosopher #1 eats a dumpling. Dumplings left: 14
Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_a released

Philosopher #2 asks waiter for chopsticks
Philosopher #1 asks waiter for chopsticks
chopstick_a grabbed
chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 13

Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_b grabbed
chopstick_a released
chopstick_a grabbed

Philosopher #2 eats a dumpling. Dumplings left: 12
Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_b released

Philosopher #1 asks waiter for chopsticks
Philosopher #2 asks waiter for chopsticks
chopstick_b grabbed
chopstick_a grabbed

Philosopher #2 eats a dumpling. Dumplings left: 11
Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_b released

chopstick_a grabbed
chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 10
Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_a released

Philosopher #1 asks waiter for chopsticks
Philosopher #2 asks waiter for chopsticks
chopstick_b grabbed
chopstick_a grabbed
Philosopher #2 eats a dumpling. Dumplings left: 9
Philosopher #2 returns chopsticks to waiter
chopstick_a released

chopstick_b released

chopstick_a grabbed
chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 8
Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_a released

Philosopher #2 asks waiter for chopsticks
Philosopher #1 asks waiter for chopsticks
chopstick_b grabbed
chopstick_a grabbed

Philosopher #2 eats a dumpling. Dumplings left: 7
Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_b released

chopstick_a grabbed
chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 6
Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_a released

Philosopher #2 asks waiter for chopsticks
Philosopher #1 asks waiter for chopsticks
chopstick_b grabbed
chopstick_a grabbed

Philosopher #2 eats a dumpling. Dumplings left: 5
Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_a grabbedchopstick_b released


chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 4
Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_a released

Philosopher #1 asks waiter for chopsticks
Philosopher #2 asks waiter for chopsticks
chopstick_a grabbed

chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 3
Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_a released

chopstick_b grabbed
chopstick_a grabbed
Philosopher #2 eats a dumpling. Dumplings left: 2
Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_b released

Philosopher #1 asks waiter for chopsticks
Philosopher #2 asks waiter for chopsticks
chopstick_a grabbed
chopstick_b grabbed
Philosopher #1 eats a dumpling. Dumplings left: 1

Philosopher #1 returns chopsticks to waiter
chopstick_b released
chopstick_b grabbed
chopstick_a released

chopstick_a grabbed
Philosopher #2 eats a dumpling. Dumplings left: 0
Philosopher #2 returns chopsticks to waiter
chopstick_a released
chopstick_b released
```

# Mission A: Dining Philosophers Problem - Arbitrator Model
### Description
Implement the Python code below in Java

### Python Code
```python

```

### Sample Output
```

```
