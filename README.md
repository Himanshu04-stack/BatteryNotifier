# 🔋 Battery Notifier (macOS)

A lightweight Java-based battery monitoring tool for macOS that keeps you informed with system notifications and Pushbullet alerts.

## ✨ Features

- 🔔 Sends notifications on every **10% battery drop/gain**
- 🔌 Notifies when the laptop is **plugged in or unplugged**
- ⚠️ Alerts when the **battery cycle count exceeds 800**
- 📈 Logs battery data in `battery_log.txt`
- ✅ Works in background and prevents system sleep using `caffeinate`

## 📦 Requirements

- macOS system
- Java installed (`java -version`)
- Pushbullet account and API key (optional, for phone notifications)

## 🚀 How to Use

1. **Clone the repo**
   ```bash
   git clone https://github.com/Himanshu04-stack/BatteryNotifier.git
   cd BatteryNotifier
