# ChitChat

A simple multi-client chat application built with Java.

## How to Run

1. Compile the files:

```powershell
javac SocketServer.java SocketClient.java
```

2. Start the server:

```powershell
java SocketServer
```

3. Connect to the server:

- A client window will open automatically
- Enter the server IP address (use `127.0.0.1` for local testing)
- Enter your nickname
- Start chatting

4. To add more clients, open a new terminal and run:

```powershell
java SocketClient
```