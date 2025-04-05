# 🔋 Battery Notifier for macOS

A lightweight and efficient Java utility that monitors your Mac's battery status and sends real-time notifications via native macOS alerts and Pushbullet. It helps extend battery health by alerting you on key battery milestones and charging events.

## 🚀 Features

- 📉 **10% interval notifications** on battery drop/gain (e.g., 90%, 80%, ... 10%)
- 🔌 Alerts when power is **plugged in** or **unplugged**
- ⚠️ Low battery warning at **20%**
- ✅ Charging complete alert at **80%**
- 📈 Logs battery percentage, charge state, and cycle count every 30 seconds
- 📬 Sends notifications via:
  - Native macOS Notification Center
  - [Pushbullet](https://www.pushbullet.com/) API
- 💤 Prevents Mac from sleeping during monitoring using `caffeinate`

## 🛠️ Requirements

- macOS with `pmset`, `system_profiler`, and `osascript` available
- Java 8 or higher
- Pushbullet Access Token (optional but recommended)

## 📦 Usage

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/BatteryNotifier.git
   cd BatteryNotifier
