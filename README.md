# LinkTCP

<p align="center">
  <img src="app/src/main/res/drawable/images/ic_icon.png" width="150" alt="LinkTCP Icon"/>
</p>

<p align="center">
  <strong>Connecting Devices. Testing Networks.</strong>
</p>

<p align="center">
  A professional TCP/IP connection testing tool for Android.
</p>

---

## Overview

LinkTCP allows you to establish TCP connections as either a **client** or a **server**, send and receive messages, and monitor network traffic in real-time. Designed with simplicity and elegance in mind.

## Features

- **Client Mode** - Connect to any TCP server by IP and port
- **Server Mode** - Start a TCP server and accept incoming connections
- **Real-time Messaging** - Send and receive messages with visual indicators
- **Traffic Statistics** - Monitor bytes sent/received
- **Connection Status** - Visual indicators (Idle, Connecting, Connected, Error)
- **Message History** - Complete log of all communications
- **Persistent Configuration** - Saves your last used settings

## Screenshots

| Connect | Log | About |
|---------|-----|-------|
| Configure and establish connections | View message history and traffic stats | App information and usage guide |

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** Clean Architecture (Domain/Data/Presentation)
- **Async:** Kotlin Coroutines + Flow
- **Persistence:** DataStore Preferences
- **Min SDK:** 28 (Android 9.0)
- **Target SDK:** 36

## Architecture

```
app/src/main/java/com/gmadariaga/linktcp/
├── domain/          # Business logic (models, repositories, use cases)
├── data/            # Data layer (TCP sockets, DataStore)
├── presentation/    # UI layer (ViewModels, Screens, Components)
├── di/              # Dependency injection
└── ui/theme/        # Material 3 theming
```

## How to Use

1. **Select Mode:** Choose `Client` to connect to a server, or `Server` to accept connections
2. **Configure:** Enter the host IP (client only) and port number
3. **Connect:** Tap Connect and watch the status indicator
4. **Communicate:** Once connected, type messages and send
5. **Monitor:** Check the Log tab for message history and traffic stats

## Build

```bash
./gradlew assembleDebug
```

## Author

**GMMadariaga**

## License

This project is available for personal and educational use.
