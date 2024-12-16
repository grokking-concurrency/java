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
