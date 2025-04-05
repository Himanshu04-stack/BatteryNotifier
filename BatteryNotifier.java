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

public class BatteryNotifier {

    private static final Set<String> notifiedEvents = new HashSet<>();
    private static int lastRoundedPercent = -1;

    public static void main(String[] args) {
        try {
            // Prevent Mac from sleeping
            Runtime.getRuntime().exec("caffeinate -dims &");
        } catch (Exception e) {
            logToFile("Error starting caffeinate: " + e.getMessage());
        }

        while (true) {
            try {
                int percentage = getBatteryPercentage();
                String status = getBatteryStatus();
                int cycleCount = getCycleCount();
                int roundedPercent = (percentage / 10) * 10;

                String logStatus = String.format("[%s] Battery: %d%% (%s), Cycle Count: %d", new Date(), percentage, status, cycleCount);
                System.out.println(logStatus);
                logToFile(logStatus);

                if (cycleCount > 800) {
                    showMacNotification("Battery Health Warning", "Cycle count is high: " + cycleCount);
                    logToFile("Battery cycle count warning sent.");
                }

                // 🔋 Notify when battery is 20% and discharging
                if (percentage == 20 && status.equals("discharging") && !notifiedEvents.contains("DISCHARGE_20")) {
                    String message = "Battery at 20% – Please plug in the charger.";
                    sendPush("Battery Low", message);
                    showMacNotification("Battery Low", message);
                    logToFile("Sent 20% warning: " + message);
                    notifiedEvents.add("DISCHARGE_20");
                }

                // ⚡ Notify when battery is 80% and charging
                if (percentage == 80 && status.equals("charging") && !notifiedEvents.contains("CHARGE_80")) {
                    String message = "Battery at 80% – Please unplug the charger.";
                    sendPush("Battery Charged", message);
                    showMacNotification("Battery Charged", message);
                    logToFile("Sent 80% charged warning: " + message);
                    notifiedEvents.add("CHARGE_80");
                }

                // 🔁 Notify every new 10% milestone
                String eventKey = status + "_" + roundedPercent;
                if (roundedPercent != lastRoundedPercent && !notifiedEvents.contains(eventKey)) {
                    String msg = "Battery is at " + percentage + "% and " + status + ".";
                    sendPush("Battery Update", msg);
                    showMacNotification("Battery Update", msg);
                    logToFile("Milestone reached: " + msg);
                    notifiedEvents.add(eventKey);
                    lastRoundedPercent = roundedPercent;
                }

                Thread.sleep(30000); // More responsive: check every 30 seconds

            } catch (Exception e) {
                logToFile("Exception: " + e.getMessage());
                e.printStackTrace();
            }
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
            String accessToken = "o.C86IhICwfwnRVFuey7Qeap4PNgkaavNS"; // Replace with your Pushbullet token
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
