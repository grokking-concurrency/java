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

# Mission B: Producer-Consumer Problem
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Implementing parking garage using semaphore for control critical section"""

import time
from threading import Thread, Semaphore, Lock

SIZE = 5
# shared memory
BUFFER = ["" for i in range(SIZE)]
producer_idx: int = 0

mutex = Lock()
empty = Semaphore(SIZE)
full = Semaphore(0)


class Producer(Thread):
    """Producer thread will produce an item and put it into the buffer"""

    def __init__(self, name: str, maximum_items: int = 5):
        super().__init__()
        self.counter = 0
        self.name = name
        self.maximum_items = maximum_items

    def next_index(self, index: int) -> int:
        """Get the next empty buffer index"""
        return (index + 1) % SIZE

    def run(self) -> None:
        global producer_idx
        while self.counter < self.maximum_items:
            # wait untill the buffer have some empty slots
            empty.acquire()
            # critical section for changing the buffer
            mutex.acquire()
            self.counter += 1
            BUFFER[producer_idx] = f"{self.name}-{self.counter}"
            print(f"{self.name} produced: "
                  f"'{BUFFER[producer_idx]}' into slot {producer_idx}")
            producer_idx = self.next_index(producer_idx)
            # leaving critical section
            mutex.release()
            # buffer have one more item to consume
            full.release()
            # simulating some real action here
            time.sleep(1)


class Consumer(Thread):
    """Consumer thread will consume items from the buffer"""

    def __init__(self, name: str, maximum_items: int = 10):
        super().__init__()
        self.name = name
        self.idx = 0
        self.counter = 0
        self.maximum_items = maximum_items

    def next_index(self) -> int:
        """Get the next buffer index to consume"""
        return (self.idx + 1) % SIZE

    def run(self) -> None:
        while self.counter < self.maximum_items:
            # wait untill the buffer has some new items to consume
            full.acquire()
            # critical section for changing the buffer
            mutex.acquire()
            item = BUFFER[self.idx]
            print(f"{self.name} consumed item: "
                  f"'{item}' from slot {self.idx}")
            self.idx = self.next_index()
            self.counter += 1
            # leaving critical section
            mutex.release()
            # one more empty slot is available in buffer
            empty.release()
            # simulating some real action here
            time.sleep(2)


if __name__ == "__main__":
    threads = [
        Producer("SpongeBob"),
        Producer("Patrick"),
        Consumer("Squidward")
    ]

    for thread in threads:
        thread.start()

    for thread in threads:
        thread.join()
```

### Sample Output
```
SpongeBob produced: 'SpongeBob-1' into slot 0
Patrick produced: 'Patrick-1' into slot 1
Squidward consumed item: 'SpongeBob-1' from slot 0
SpongeBob produced: 'SpongeBob-2' into slot 2
Patrick produced: 'Patrick-2' into slot 3
Squidward consumed item: 'Patrick-1' from slot 1
SpongeBob produced: 'SpongeBob-3' into slot 4
Patrick produced: 'Patrick-3' into slot 0
SpongeBob produced: 'SpongeBob-4' into slot 1
Squidward consumed item: 'SpongeBob-2' from slot 2
Patrick produced: 'Patrick-4' into slot 2
Squidward consumed item: 'Patrick-2' from slot 3
SpongeBob produced: 'SpongeBob-5' into slot 3
Squidward consumed item: 'SpongeBob-3' from slot 4
Patrick produced: 'Patrick-5' into slot 4
Squidward consumed item: 'Patrick-3' from slot 0
Squidward consumed item: 'SpongeBob-4' from slot 1
Squidward consumed item: 'Patrick-4' from slot 2
Squidward consumed item: 'SpongeBob-5' from slot 3
Squidward consumed item: 'Patrick-5' from slot 4
```

# Mission C: Reader-Writer Problem
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Reader-writer lock: readers have priority"""

import time
import random
from threading import Thread, Lock

class RWLock:
    """    A read-write lock that allows multiple readers or one
    writer at a time."""
    def __init__(self) -> None:
        self.readers = 0
        self.read_lock = Lock()
        self.write_lock = Lock()

    def acquire_read(self) -> None:
        """Acquires the read lock for the current thread.
        If there is a writer waiting for the lock, the method blocks until
        the writer releases the lock."""
        self.read_lock.acquire()
        self.readers += 1
        if self.readers == 1:
            self.write_lock.acquire()
        self.read_lock.release()

    def release_read(self) -> None:
        """Releases the read lock held by the current thread.
        If there are no more readers holding the lock, the method releases
        the write lock."""
        assert self.readers >= 1
        self.read_lock.acquire()
        self.readers -= 1
        if self.readers == 0:
            self.write_lock.release()
        self.read_lock.release()

    def acquire_write(self) -> None:
        """Acquires the write lock for the current thread.
        If there is a reader or a writer holding the lock, the method
        blocks until the lock is released."""
        self.write_lock.acquire()

    def release_write(self) -> None:
        """Releases the write lock held by the current thread."""
        self.write_lock.release()

"""Readers-writers problem using mutex:
Readers may proceed if no writer is writing.
Writers may proceed if no reader is reading and no other writer is writing.
With a simple RWLock, Readers may be starved by a Writer.
"""

# try fair RWLock
# from rwlock_fair import RWLockFair as RWLock

# shared memory
counter = 0
lock = RWLock()


class User(Thread):
    """User of the library catalog. Reader implementation"""

    def __init__(self, idx: int):
        super().__init__()
        self.idx = idx

    def run(self) -> None:
        while True:
            lock.acquire_read()

            print(f"User {self.idx} reading: {counter}")
            time.sleep(random.randrange(1, 3))

            lock.release_read()
            # simulating some real action here
            time.sleep(0.5)


class Librarian(Thread):
    """Writer of the library catalog. Writer implementation"""

    def run(self) -> None:
        global counter
        while True:
            lock.acquire_write()

            print("Librarian writing...")
            counter += 1
            print(f"New value: {counter}")
            # simulating some real action here
            time.sleep(random.randrange(1, 3))

            lock.release_write()


if __name__ == "__main__":
    threads = [
        User(0),
        User(1),
        Librarian()
    ]

    for thread in threads:
        thread.start()

    for thread in threads:
        thread.join()
```

### Sample Output
```
User 0 reading: 0
User 1 reading: 0
User 0 reading: 0
User 1 reading: 0
Librarian writing...
New value: 1
Librarian writing...
New value: 2
Librarian writing...
New value: 3
User 1 reading: 3
User 0 reading: 3
Librarian writing...
New value: 4
User 1 reading: 4
User 0 reading: 4
User 0 reading: 4
Librarian writing...
New value: 5
User 0 reading: 5
User 1 reading: 5
User 0 reading: 5
User 1 reading: 5
```
