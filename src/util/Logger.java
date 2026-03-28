package util;

import model.Message;
import java.util.ArrayList;
import java.util.List;

public class Logger {

    private static Logger instance;
    private final List<String> logs = new ArrayList<>();

    //only one logger exist
    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void log(Message message) {
        String entry = message.toString();
        logs.add(entry);
        System.out.println("[LOG] " + entry);
    }

    public void logSystem(String text) {
        String entry = "[SYSTEM] " +text;
        logs.add(entry);
    }

    public List<String> getAllLogs() {
        return new ArrayList<>(logs);
    }
}
