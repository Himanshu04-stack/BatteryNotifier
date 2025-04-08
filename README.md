# 🔋 BatteryNotifier for macOS

A Java-based background utility that monitors your MacBook's battery status and provides timely **notifications**, **logs**, and **Pushbullet alerts** to help you maintain battery health and stay informed.

## 📌 Features

- 🚨 **Notifications on Critical Battery Levels**  
  - 🔌 Plug in reminder at **20%**
  - 🔋 Unplug reminder at **80%**

- ♻️ **Continuous Alerts Between Thresholds**  
  - Charging: 80–85%  
  - Discharging: 25–20%  

- 🔄 **10% Drop/Gain Notifications**  
  - Alerts on every 10% change in battery level

- ⚡ **Charger Connect/Disconnect Alerts**

- 📉 **Battery Cycle Count Monitoring**  
  - Warns when cycle count exceeds **800**

- 🖥️ **Native macOS Notifications using `osascript`**

- ☁️ **Push Notifications via Pushbullet**

- 📝 **Battery Logs with Timestamp**

---

## 🚀 How It Works

Runs a background Java process that:
1. Monitors battery status using macOS `pmset` and `system_profiler` commands.
2. Sends native notifications and Pushbullet alerts.
3. Logs events in a `battery_log.txt` file.
4. Uses `caffeinate` to keep your Mac awake.

---

## 📦 Requirements

- macOS
- Java 8+ installed
- Pushbullet account (for push notifications)

---

## 🔧 Setup

### 1. Clone the Repo
```bash
git clone https://github.com/your-username/BatteryNotifier.git
cd BatteryNotifier
