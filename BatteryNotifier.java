import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class BatteryNotifier {

    private static final Set<String> notifiedEvents = new HashSet<>();
    private static int lastRoundedPercent = -1;
    private static boolean wasCharging = false;
    private static Timer continuousNotifier = new Timer();
    private static boolean continuousRunning = false;

    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("caffeinate -dims &");
        } catch (Exception e) {
            logToFile("Error starting caffeinate: " + e.getMessage());
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    int percentage = getBatteryPercentage();
                    String status = getBatteryStatus();
                    int cycleCount = getCycleCount();
                    int roundedPercent = (percentage / 10) * 10;

                    String logStatus = String.format("[%s] Battery: %d%% (%s), Cycle Count: %d", new Date(), percentage, status, cycleCount);
                    System.out.println(logStatus);
                    logToFile(logStatus);

                    boolean isCharging = status.equals("charging");

                    // Notify on charge state change
                    if (isCharging != wasCharging) {
                        String msg = isCharging ? "Charger Connected" : "Charger Disconnected";
                        showMacNotification("Charger Status", msg);
                        sendPush("Charger Status", msg);
                        logToFile(msg);
                        wasCharging = isCharging;
                    }

                    if (cycleCount > 800) {
                        showMacNotification("Battery Health Warning", "Cycle count is high: " + cycleCount);
                        logToFile("Battery cycle count warning sent.");
                    }

                    if (percentage == 20 && status.equals("discharging") && !notifiedEvents.contains("DISCHARGE_20")) {
                        notifyBattery("Battery Low", "Battery at 20% – Please plug in the charger.", "DISCHARGE_20");
                    }

                    if (percentage == 80 && status.equals("charging") && !notifiedEvents.contains("CHARGE_80")) {
                        notifyBattery("Battery Charged", "Battery at 80% – Please unplug the charger.", "CHARGE_80");
                    }

                    String eventKey = status + "_" + roundedPercent;
                    if (roundedPercent != lastRoundedPercent && !notifiedEvents.contains(eventKey)) {
                        notifyBattery("Battery Update", "Battery is at " + percentage + "% and " + status + ".", eventKey);
                        lastRoundedPercent = roundedPercent;
                    }

                    if ((isCharging && percentage >= 80 && percentage <= 85) || (!isCharging && percentage <= 25 && percentage >= 20)) {
                        if (!continuousRunning) {
                            startContinuousNotifications(status, percentage);
                        }
                    } else {
                        stopContinuousNotifications();
                    }

                } catch (Exception e) {
                    logToFile("Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 0, 30 * 1000);
    }

    private static void notifyBattery(String title, String message, String key) {
        sendPush(title, message);
        showMacNotification(title, message);
        logToFile("Sent notification: " + message);
        notifiedEvents.add(key);
    }

    private static void startContinuousNotifications(String status, int percent) {
        continuousRunning = true;
        continuousNotifier = new Timer();
        String title = status.equals("charging") ? "Battery Nearly Full" : "Battery Low";
        String message = status.equals("charging") ?
                "Battery between 80–85%. Consider unplugging charger." :
                "Battery between 20–25%. Consider plugging in charger.";
        continuousNotifier.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                showMacNotification(title, message);
                sendPush(title, message);
                logToFile("Continuous alert: " + message);
            }
        }, 0, 60 * 1000);
    }

    private static void stopContinuousNotifications() {
        if (continuousRunning) {
            continuousNotifier.cancel();
            continuousNotifier.purge();
            continuousRunning = false;
            logToFile("Stopped continuous notifications.");
        }
    }

    private static int getBatteryPercentage() throws Exception {
        String[] cmd = { "/bin/bash", "-c", "pmset -g batt | grep -Eo '\\d+%' | cut -d% -f1" };
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return Integer.parseInt(reader.readLine().trim());
    }

    private static String getBatteryStatus() throws Exception {
        String[] cmd = { "/bin/bash", "-c", "pmset -g batt | grep -o 'discharging\\|charging\\|charged'" };
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return reader.readLine().trim().toLowerCase();
    }

    private static int getCycleCount() throws Exception {
        String[] cmd = { "/bin/bash", "-c", "system_profiler SPPowerDataType | grep 'Cycle Count' | awk '{print $3}'" };
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return Integer.parseInt(reader.readLine().trim());
    }

    private static void sendPush(String title, String body) {
        try {
            String accessToken = "import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class BatteryNotifier {

    private static final Set<String> notifiedEvents = new HashSet<>();
    private static int lastRoundedPercent = -1;
    private static boolean wasCharging = false;
    private static Timer continuousNotifier = new Timer();
    private static boolean continuousRunning = false;

    public static void main(String[] args) {
        try {
            Runtime.getRuntime().exec("caffeinate -dims &");
        } catch (Exception e) {
            logToFile("Error starting caffeinate: " + e.getMessage());
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    int percentage = getBatteryPercentage();
                    String status = getBatteryStatus();
                    int cycleCount = getCycleCount();
                    int roundedPercent = (percentage / 10) * 10;

                    String logStatus = String.format("[%s] Battery: %d%% (%s), Cycle Count: %d", new Date(), percentage, status, cycleCount);
                    System.out.println(logStatus);
                    logToFile(logStatus);

                    boolean isCharging = status.equals("charging");

                    // Notify on charge state change
                    if (isCharging != wasCharging) {
                        String msg = isCharging ? "Charger Connected" : "Charger Disconnected";
                        showMacNotification("Charger Status", msg);
                        sendPush("Charger Status", msg);
                        logToFile(msg);
                        wasCharging = isCharging;
                    }

                    if (cycleCount > 800) {
                        showMacNotification("Battery Health Warning", "Cycle count is high: " + cycleCount);
                        logToFile("Battery cycle count warning sent.");
                    }

                    if (percentage == 20 && status.equals("discharging") && !notifiedEvents.contains("DISCHARGE_20")) {
                        notifyBattery("Battery Low", "Battery at 20% – Please plug in the charger.", "DISCHARGE_20");
                    }

                    if (percentage == 80 && status.equals("charging") && !notifiedEvents.contains("CHARGE_80")) {
                        notifyBattery("Battery Charged", "Battery at 80% – Please unplug the charger.", "CHARGE_80");
                    }

                    String eventKey = status + "_" + roundedPercent;
                    if (roundedPercent != lastRoundedPercent && !notifiedEvents.contains(eventKey)) {
                        notifyBattery("Battery Update", "Battery is at " + percentage + "% and " + status + ".", eventKey);
                        lastRoundedPercent = roundedPercent;
                    }

                    if ((isCharging && percentage >= 80 && percentage <= 85) || (!isCharging && percentage <= 25 && percentage >= 20)) {
                        if (!continuousRunning) {
                            startContinuousNotifications(status, percentage);
                        }
                    } else {
                        stopContinuousNotifications();
                    }

                } catch (Exception e) {
                    logToFile("Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 0, 30 * 1000);
    }

    private static void notifyBattery(String title, String message, String key) {
        sendPush(title, message);
        showMacNotification(title, message);
        logToFile("Sent notification: " + message);
        notifiedEvents.add(key);
    }

    private static void startContinuousNotifications(String status, int percent) {
        continuousRunning = true;
        continuousNotifier = new Timer();
        String title = status.equals("charging") ? "Battery Nearly Full" : "Battery Low";
        String message = status.equals("charging") ?
                "Battery between 80–85%. Consider unplugging charger." :
                "Battery between 20–25%. Consider plugging in charger.";
        continuousNotifier.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                showMacNotification(title, message);
                sendPush(title, message);
                logToFile("Continuous alert: " + message);
            }
        }, 0, 60 * 1000);
    }

    private static void stopContinuousNotifications() {
        if (continuousRunning) {
            continuousNotifier.cancel();
            continuousNotifier.purge();
            continuousRunning = false;
            logToFile("Stopped continuous notifications.");
        }
    }

    private static int getBatteryPercentage() throws Exception {
        String[] cmd = { "/bin/bash", "-c", "pmset -g batt | grep -Eo '\\d+%' | cut -d% -f1" };
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return Integer.parseInt(reader.readLine().trim());
    }

    private static String getBatteryStatus() throws Exception {
        String[] cmd = { "/bin/bash", "-c", "pmset -g batt | grep -o 'discharging\\|charging\\|charged'" };
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return reader.readLine().trim().toLowerCase();
    }

    private static int getCycleCount() throws Exception {
        String[] cmd = { "/bin/bash", "-c", "system_profiler SPPowerDataType | grep 'Cycle Count' | awk '{print $3}'" };
        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return Integer.parseInt(reader.readLine().trim());
    }

    private static void sendPush(String title, String body) {
        try {
            String accessToken = "YOUR PUSHBULLET TOKEN"; // Replace with your Pushbullet token
            URI uri = URI.create("https://api.pushbullet.com/v2/pushes");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Access-Token", accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = String.format("{\"type\": \"note\", \"title\": \"%s\", \"body\": \"%s\"}", title, body);
            byte[] out = payload.getBytes(StandardCharsets.UTF_8);
            conn.getOutputStream().write(out);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while (in.readLine() != null) {}
            in.close();
            conn.disconnect();

            logToFile("Pushbullet notification sent: " + title);

        } catch (Exception e) {
            logToFile("Failed to send Pushbullet notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMacNotification(String title, String message) {
        try {
            String script = String.format("display notification \"%s\" with title \"%s\"", message, title);
            String[] cmd = { "osascript", "-e", script };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            logToFile("Failed to send Mac notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void logToFile(String message) {
        try (FileWriter fw = new FileWriter("battery_log.txt", true)) {
            fw.write("[" + new Date() + "] " + message + "\n");
        } catch (Exception e) {
            System.err.println("Logging failed: " + e.getMessage());
        }
    }
}
"; // Replace with your Pushbullet token
            URI uri = URI.create("https://api.pushbullet.com/v2/pushes");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Access-Token", accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = String.format("{\"type\": \"note\", \"title\": \"%s\", \"body\": \"%s\"}", title, body);
            byte[] out = payload.getBytes(StandardCharsets.UTF_8);
            conn.getOutputStream().write(out);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while (in.readLine() != null) {}
            in.close();
            conn.disconnect();

            logToFile("Pushbullet notification sent: " + title);

        } catch (Exception e) {
            logToFile("Failed to send Pushbullet notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showMacNotification(String title, String message) {
        try {
            String script = String.format("display notification \"%s\" with title \"%s\"", message, title);
            String[] cmd = { "osascript", "-e", script };
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            logToFile("Failed to send Mac notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void logToFile(String message) {
        try (FileWriter fw = new FileWriter("battery_log.txt", true)) {
            fw.write("[" + new Date() + "] " + message + "\n");
        } catch (Exception e) {
            System.err.println("Logging failed: " + e.getMessage());
        }
    }
}
