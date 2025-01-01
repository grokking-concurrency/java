# Mission A: Asynchronous Pizza Server
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9

"""Non-blocking pizza joint implementation using asyncio library"""

import asyncio
import socket

# the maximum amount of data to be received at once
BUFFER_SIZE = 1024
ADDRESS = ("127.0.0.1", 12345)  # address and port of the host machine

class Kitchen:
    @staticmethod
    def cook_pizza(n):
        print(f"Started cooking {n} pizzas")
        time.sleep(n)
        print(f"Fresh {n} pizzas are ready!")

class Server:
    def __init__(self, event_loop: asyncio.AbstractEventLoop) -> None:
        self.event_loop = event_loop
        print(f"Starting up at: {ADDRESS}")
        self.server_socket = socket.create_server(ADDRESS)
        # set socket to non-blocking mode
        self.server_socket.setblocking(False)

    async def start(self) -> None:
        print("Server listening for incoming connections")
        try:
            while True:
                conn, client_address = \
                    await self.event_loop.sock_accept(
                        self.server_socket)
                self.event_loop.create_task(self.serve(conn))
        except Exception:
            self.server_socket.close()
            print("\nServer stopped.")

    async def serve(self, conn) -> None:
        while True:
            data = await self.event_loop.sock_recv(conn, BUFFER_SIZE)
            if not data:
                break
            try:
                order = int(data.decode())
                response = f"Thank you for ordering {order} pizzas!\n"
                print(f"Sending message to {conn.getpeername()}")
                await self.event_loop.sock_sendall(
                    conn, f"{response}".encode())
                await self.event_loop.run_in_executor(
                    None, Kitchen.cook_pizza, order)
                response = f"Your order of {order} pizzas is ready!\n"
            except ValueError:
                response = "Wrong number of pizzas, please try again\n"

            print(f"Sending message to {conn.getpeername()}")
            await self.event_loop.sock_sendall(conn, response.encode())
        print(f"Connection with {conn.getpeername()} has been closed")
        conn.close()


if __name__ == "__main__":
    event_loop = asyncio.get_event_loop()
    server = Server(event_loop=event_loop)
    event_loop.create_task(server.start())
    event_loop.run_forever()
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
