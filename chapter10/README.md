# Mission A: Threaded Server
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Threaded echo server implementation"""

from socket import socket, create_server
from threading import Thread

# the maximum amount of data to be received at once
BUFFER_SIZE = 1024
ADDRESS = ("127.0.0.1", 12345)   # address and port of the host machine


class Handler(Thread):
    def __init__(self, conn: socket):
        super().__init__()
        self.conn = conn

    def run(self) -> None:
        """Serve the incoming connection in a thread by sending and
        receiving data."""
        print(f"Connected to {self.conn.getpeername()}")
        try:
            while True:
                data = self.conn.recv(BUFFER_SIZE)
                if not data:
                    break
                try:
                    order = int(data.decode())
                    response = f"Thank you for ordering {order} pizzas!\n"
                except ValueError:
                    response = "Wrong number of pizzas, please try again\n"
                print(f"Sending message to {self.conn.getpeername()}")
                # send a response
                self.conn.send(response.encode())
        finally:
            # server expects the client to close its side of the connection
            # when it’s done. In a real application, we should use timeout for
            # clients if they don’t send a request after a certain amount of time.
            # a request after a certain amount of time.
            print(f"Connection with {self.conn.getpeername()} "
                  f"has been closed")
            self.conn.close()


class Server:
    def __init__(self) -> None:
        try:
            print(f"Starting up at: {ADDRESS}")
            self.server_socket = create_server(ADDRESS)
        except OSError:
            self.server_socket.close()
            print("\nServer stopped.")

    def start(self) -> None:
        """Start the server by continuously accepting and serving incoming
        connections."""
        print("Server listening for incoming connections")
        try:
            while True:
                conn, address = self.server_socket.accept()
                print(f"Client connection request from {address}")
                thread = Handler(conn)
                thread.start()
        finally:
            self.server_socket.close()
            print("\nServer stopped.")


if __name__ == "__main__":
    server = Server()
    server.start()
```

### Sample Output
#### Server
```
Starting up at: ('127.0.0.1', 12345)
Server listening for incoming connections
Client connection request from ('127.0.0.1', 57151)
Connected to ('127.0.0.1', 57151)
Sending message to ('127.0.0.1', 57151)
Client connection request from ('127.0.0.1', 57152)
Connected to ('127.0.0.1', 57152)
Sending message to ('127.0.0.1', 57152)
Sending message to ('127.0.0.1', 57152)

```
#### Terminal
```
nc 127.0.0.1 12345
pizzzza
Wrong number of pizzas, please try again
12
Thank you for ordering 12 pizzas!
```

# Mission B: Busy-waiting non-blocking server
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Busy-waiting non-blocking server implementation"""

import typing as T
from socket import socket, create_server

# the maximum amount of data to be received at once
BUFFER_SIZE = 1024
ADDRESS = ("127.0.0.1", 12345)   # address and port of the host machine


class Server:
    clients: T.Set[socket] = set()

    def __init__(self) -> None:
        try:
            print(f"Starting up at: {ADDRESS}")
            self.server_socket = create_server(ADDRESS)
            # set socket to non-blocking mode
            self.server_socket.setblocking(False)
        except OSError:
            self.server_socket.close()
            print("\nServer stopped.")

    def accept(self) -> None:
        try:
            conn, address = self.server_socket.accept()
            print(f"Connected to {address}")
            # making this connection non-blocking
            conn.setblocking(False)
            self.clients.add(conn)
        except BlockingIOError:
            # [Errno 35] Resource temporarily unavailable
            # indicates that "accept" returned without results
            pass

    def serve(self, conn: socket) -> None:
        """Serve the incoming connection by sending and receiving data."""
        try:
            while True:
                data = conn.recv(BUFFER_SIZE)
                if not data:
                    break
                try:
                    order = int(data.decode())
                    response = f"Thank you for ordering {order} pizzas!\n"
                except ValueError:
                    response = "Wrong number of pizzas, please try again\n"
                print(f"Sending message to {conn.getpeername()}")
                # send a response
                conn.send(response.encode())
        except BlockingIOError:
            # recv/send returns without data
            pass

    def start(self) -> None:
        """Start the server by continuously accepting and serving incoming
        connections."""
        print("Server listening for incoming connections")
        try:
            while True:
                self.accept()
                for conn in self.clients.copy():
                    self.serve(conn)
        finally:
            self.server_socket.close()
            print("\nServer stopped.")


if __name__ == "__main__":
    server = Server()
    server.start()
```

### Sample Output
#### Server
```
Starting up at: ('127.0.0.1', 12345)
Server listening for incoming connections
Connected to ('127.0.0.1', 57283)
Sending message to ('127.0.0.1', 57283)
Sending message to ('127.0.0.1', 57283)
Sending message to ('127.0.0.1', 57283)
```
### Terminal
```
nc 127.0.0.1 12345
a
Wrong number of pizzas, please try again
123
Thank you for ordering 123 pizzas!
11
Thank you for ordering 11 pizzas!
```
