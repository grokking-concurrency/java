# Mission A: Shared Memory
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Using shared memory IPC between threads"""

import time
from threading import Thread, current_thread

SIZE = 5
# setup shared memory
shared_memory = [-1] * SIZE


class Producer(Thread):
    def run(self) -> None:
        self.name = "Producer"
        global shared_memory
        for i in range(SIZE):
            print(f"{current_thread().name}: Writing {int(i)}")
            shared_memory[i - 1] = i


class Consumer(Thread):
    def run(self) -> None:
        self.name = "Consumer"
        global shared_memory
        for i in range(SIZE):
            # try reading the data until succession
            while True:
                line = shared_memory[i]
                if line == -1:
                    # data hasn't change - waiting for a second
                    print(f"{current_thread().name}: Data not available\n"
                          f"Sleeping for 1 second before retrying")
                    time.sleep(1)
                    continue
                print(f"{current_thread().name}: Read: {int(line)}")
                break


def main() -> None:
    threads = [
        Consumer(),
        Producer(),
    ]

    # start threads
    for thread in threads:
        thread.start()

    # block the main thread until the child threads has finished
    for thread in threads:
        thread.join()


if __name__ == "__main__":
    main()
```

### Sample Output
```
Consumer: Data not available
Sleeping for 1 second before retrying
Producer: Writing 0
Producer: Writing 1
Producer: Writing 2
Producer: Writing 3
Producer: Writing 4
Consumer: Read: 1
Consumer: Read: 2
Consumer: Read: 3
Consumer: Read: 4
Consumer: Read: 0
```


# Mission B: Unnamed Pipe
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Using pipes for IPC between threads"""

from threading import Thread, current_thread
from multiprocessing import Pipe
from multiprocessing.connection import Connection


class Writer(Thread):
    """Writer thread will write messages into the pipe"""
    def __init__(self, conn: Connection):
        super().__init__()
        self.conn = conn
        self.name = "Writer"

    def run(self) -> None:
        print(f"{current_thread().name}: Sending rubber duck...")
        self.conn.send("Rubber duck")


class Reader(Thread):
    """Reader thread will read messages from the pipe"""
    def __init__(self, conn: Connection):
        super().__init__()
        self.conn = conn
        self.name = "Reader"

    def run(self) -> None:
        print(f"{current_thread().name}: Reading...")
        msg = self.conn.recv()
        print(f"{current_thread().name}: Received: {msg}")


def main() -> None:
    # Connections for reading and writing
    reader_conn, writer_conn = Pipe()
    reader = Reader(reader_conn)
    writer = Writer(writer_conn)

    threads = [
        writer,
        reader
    ]
    # start threads
    for thread in threads:
        thread.start()

    # block the main thread until the child threads has finished
    for thread in threads:
        thread.join()


if __name__ == "__main__":
    main()
```

### Sample Output
```
Writer: Sending rubber duck...
Reader: Reading...
Reader: Received: Rubber duck
```

# Mission C: Socket
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

""" Using sockets for IPC """

import socket
import os.path
import time
from threading import Thread, current_thread

# in Unix everything is a file
SOCK_FILE = "./mailbox"
BUFFER_SIZE = 1024


class Sender(Thread):
    def run(self) -> None:
        self.name = "Sender"
        # AF_UNIX (Unix domain socket) and SOCK_STREAM are constants
        # that represent the socket family and socket type respectively
        client = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
        client.connect(SOCK_FILE)

        messages = ["Hello", " ", "world!"]
        for msg in messages:
            print(f"{current_thread().name}: Send: '{msg}'")
            client.sendall(str.encode(msg))

        client.close()


class Receiver(Thread):
    def run(self) -> None:
        self.name = "Receiver"
        server = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
        # bind socket to the file
        server.bind(SOCK_FILE)
        # let's start listening mode for this socket
        server.listen()

        print(f"{current_thread().name}: Listening for incoming messages...")
        # accept a connection
        conn, addr = server.accept()

        while True:
            # receive data from socket
            data = conn.recv(BUFFER_SIZE)
            if not data:
                break
            message = data.decode()
            print(f"{current_thread().name}: Received: '{message}'")

        server.close()


def main() -> None:
    # verify if exists the sock file
    if os.path.exists(SOCK_FILE):
        os.remove(SOCK_FILE)

    # receiver will create a socket and socket file
    receiver = Receiver()
    receiver.start()
    # waiting untill the socket has been created
    time.sleep(1)
    sender = Sender()
    sender.start()

    # block the main thread until the child threads has finished
    for thread in [receiver, sender]:
        thread.join()

    # cleaning up
    os.remove(SOCK_FILE)


if __name__ == "__main__":
    main()
```

### Sample Output
```
Receiver: Listening for incoming messages...
Sender: Send: 'Hello'
Sender: Send: ' '
Sender: Send: 'world!'
Receiver: Received: 'Hello world!'
```

# Mission D: Thread Pool
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Simple thread pool implementation"""

import time
import queue
import typing as T
from threading import Thread, current_thread

Callback = T.Callable[..., None]
Task = T.Tuple[Callback, T.Any, T.Any]
TaskQueue = queue.Queue


class Worker(Thread):
    """Thread executing tasks from a given tasks queue"""
    def __init__(self, tasks: queue.Queue[Task]):
        super().__init__()
        self.tasks = tasks

    def run(self) -> None:
        # running the thread indefinitely
        while True:
            # getting the tasks from queue and execute
            func, args, kargs = self.tasks.get()
            try:
                func(*args, **kargs)
            except Exception as e:
                print(e)
            self.tasks.task_done()


class ThreadPool:
    """Pool of threads consuming tasks from a queue"""
    def __init__(self, num_threads: int):
        # setting up the queue to put tasks
        self.tasks: TaskQueue = queue.Queue(num_threads)
        self.num_threads = num_threads

        # create long-running threads
        for _ in range(self.num_threads):
            worker = Worker(self.tasks)
            worker.setDaemon(True)
            worker.start()

    def submit(self, func: Callback, *args, **kargs) -> None:
        """Add a task to the queue"""
        self.tasks.put((func, args, kargs))

    def wait_completion(self) -> None:
        """Wait for completion of all the tasks in the queue"""
        # join method that blocks the main thread until the child
        # threads has finished
        self.tasks.join()


def cpu_waster(i: int) -> None:
    """Wasting the processor time, professionally"""
    name = current_thread().getName()
    print(f"{name} doing {i} work")
    time.sleep(3)


def main() -> None:
    pool = ThreadPool(num_threads=5)
    for i in range(20):
        pool.submit(cpu_waster, i)

    print("All work requests sent")
    pool.wait_completion()
    print("All work completed")


if __name__ == "__main__":
    main()
```

### Sample Output
```
Thread-2 doing 1 work
Thread-5 doing 2 work
Thread-1 doing 3 work
Thread-3 doing 4 work
Thread-4 doing 0 work
Thread-2 doing 5 work
Thread-5 doing 6 work
Thread-1 doing 7 work
Thread-4 doing 9 work
Thread-3 doing 8 work
Thread-2 doing 10 work
Thread-5 doing 11 work
Thread-1 doing 12 work
Thread-4 doing 13 work
Thread-3 doing 14 work
All work requests sent
Thread-2 doing 15 work
Thread-5 doing 16 work
Thread-1 doing 17 work
Thread-3 doing 18 work
Thread-4 doing 19 work
All work completed
```


# Mission E: Password Crack
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Program for cracking the password consisting of only numbers using multi
cores in parallel"""

import os
import math
import time
import typing as T
import hashlib
from multiprocessing import Pool

ChunkRange = T.Tuple[int, int]


def get_combinations(*, length: int, min_number: int = 0, max_number: int = None) -> T.List[str]:
    """Generate all possible password combinations"""
    combinations = []
    if not max_number:
        # calculating maximum number based on the length
        max_number = int(math.pow(10, length) - 1)

    # go through all possible combinations in a given range
    for i in range(min_number, max_number + 1):
        str_num = str(i)
        # fill in the missing numbers with zeros
        zeros = "0" * (length - len(str_num))
        combinations.append("".join((zeros, str_num)))
    return combinations


def get_crypto_hash(password: str) -> str:
    """"Calculating cryptographic hash of the password"""
    return hashlib.sha256(password.encode()).hexdigest()


def check_password(expected_crypto_hash: str,
                   possible_password: str) -> bool:
    actual_crypto_hash = get_crypto_hash(possible_password)
    # compare the resulted cryptographic hash with the one stored on the system
    return expected_crypto_hash == actual_crypto_hash


def get_chunks(num_ranges: int,
               length: int) -> T.Iterator[ChunkRange]:
    """Splitting the passwords into chunks using break points"""
    max_number = int(math.pow(10, length) - 1)
    chunk_starts = [int(max_number / num_ranges * i)
                    for i in range(num_ranges)]
    chunk_ends = [start_point - 1
                  for start_point in
                  chunk_starts[1:]] + [max_number]
    return zip(chunk_starts, chunk_ends)


def crack_chunk(crypto_hash: str, length: int, chunk_start: int,
                chunk_end: int) -> T.Union[str, None]:
    """Brute force the password combinations"""
    print(f"Processing {chunk_start} to {chunk_end}")
    combinations = get_combinations(length=length, min_number=chunk_start,
                                    max_number=chunk_end)
    for combination in combinations:
        if check_password(crypto_hash, combination):
            return combination  # found it
    return  # not found


def crack_password_parallel(crypto_hash: str, length: int) -> None:
    """Orchestrate cracking the password between different processes"""
    # getting number of available processors
    num_cores = os.cpu_count()
    print("Processing number combinations concurrently")
    start_time = time.perf_counter()

    # processing each chunk in a separate process concurrently
    with Pool() as pool:
        arguments = ((crypto_hash, length, chunk_start, chunk_end) for
                     chunk_start, chunk_end in
                     get_chunks(num_cores, length))
        results = pool.starmap(crack_chunk, arguments)
        print("Waiting for chunks to finish")
        pool.close()
        pool.join()

    result = [res for res in results if res]
    print(f"PASSWORD CRACKED: {result[0]}")
    process_time = time.perf_counter() - start_time
    print(f"PROCESS TIME: {process_time}")


if __name__ == "__main__":
    crypto_hash = \
        "e24df920078c3dd4e7e8d2442f00e5c9ab2a231bb3918d65cc50906e49ecaef4"
    length = 8
    crack_password_parallel(crypto_hash, length)
```

### Sample Output
```
Processing number combinations concurrently
Processing 0 to 8333332
Processing 8333333 to 16666665
Processing 16666666 to 24999998
Processing 24999999 to 33333332
Processing 33333333 to 41666665
Processing 41666666 to 49999998
Processing 49999999 to 58333331
Processing 58333332 to 66666665
Processing 66666666 to 74999998
Processing 74999999 to 83333331
Processing 83333332 to 91666664
Processing 91666665 to 99999999
Waiting for chunks to finish
PASSWORD CRACKED: 87654321
PROCESS TIME: 16.330762699999998
```
