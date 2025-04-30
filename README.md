# dotsandboxes
 
# 🎮 Dots and Boxes – JavaFX Desktop Game

A modern desktop version of the classic **Dots and Boxes** game built using **JavaFX**, with support for:
- Local Multiplayer
- Computer Opponent (3 difficulty levels)
- Facebook Login via Firebase
- Online play with Facebook Friends
- Polished UI and Sound Effects

> 🧠 Developed as part of a Software Group Project (B.Tech IT – Semester 4)

---

## 🖼️ Preview

![Game Board](./screenshots/board.png)  
![Main Menu](./screenshots/menu.png)

---

## 🚀 Features

- 🎮 **Game Modes**
  - `Play Locally`: Two players on the same system
  - `Play with Computer`: Solo mode with Easy, Medium, Hard difficulties
  - `Play Online`: Connect with Facebook friends

- 🔐 **Facebook Login**
  - Implemented using Facebook Developer & Firebase Authentication

- 🔊 **Sound Effects**
  - Line drawn, box completed, button click, win/lose/tie

- 🧠 **Computer Opponent**
  - Logic-based bot (not AI) with three strategies:
    - Easy: Random moves
    - Medium: Prioritize completing boxes
    - Hard: Defensive and offensive move analysis

- 🖥️ **Built With**
  - Java 17+
  - JavaFX 22
  - Firebase Admin SDK
  - NanoHTTPD (for local redirect server)
  - Gson for JSON handling

---

## 📦 Download

👉 Download the latest packaged game setup from:

🔗 [Pixel Download Hub – GitHub Repository](https://github.com/Kavi7605/pixel-download-hub)

---

## 🛠️ Getting Started (Developers)

### Prerequisites
- Java JDK 17 or higher
- Maven
- JavaFX SDK 22 ([Download here](https://gluonhq.com/products/javafx/))

### Clone and Build

```bash
git clone https://github.com/Kavi7605/dotsandboxes
cd dotsandboxes
mvn clean package
```
