# Mission A
### Description
Implement the Python code below in Java

### Python Code
```python
#!/usr/bin/env python3.9
"""Threads that waste CPU cycles"""

import os
import time
import threading
from threading import Thread

def cpu_waster(i: int) -> None:
    """Wasting the processor time, professionally"""
    # getting current thread name
    name = threading.current_thread().getName()
    print(f"{name} doing {i} work")
    time.sleep(3)

def display_threads() -> None:
    """Display information about current process"""
    print("-" * 10)
    print(f"Current process PID: {os.getpid()}")
    print(f"Thread Count: {threading.active_count()}")
    print("Active threads:")
    for thread in threading.enumerate():
        print(thread)

def main(num_threads: int) -> None:
    display_threads()

    print(f"Starting {num_threads} CPU wasters...")
    for i in range(num_threads):
        # creating and starting the thread
        thread = Thread(target=cpu_waster, args=(i,))
        thread.start()

    display_threads()

if __name__ == "__main__":
    num_threads = 5
    main(num_threads)
```

### Sample Output
```
----------
Current process PID: 1241
Thread Count: 1
Active threads:
<_MainThread(MainThread, started 139743184895304)>
Starting 5 CPU wasters...
Thread-1 doing 0 work
Thread-2 doing 1 work
Thread-3 doing 2 work
Thread-4 doing 3 work
Thread-5 doing 4 work
----------
Current process PID: 1241
Thread Count: 6
Active threads:
<_MainThread(MainThread, started 139743184895304)>
<Thread(Thread-1, started 139743179852576)>
<Thread(Thread-2, started 139743178529568)>
<Thread(Thread-3, started 139743177468704)>
<Thread(Thread-4, started 139743176407840)>
<Thread(Thread-5, started 139743175346976)>
```
