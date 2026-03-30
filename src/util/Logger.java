package util;

//for printing system messages
public class Logger {

    private static Logger instance;

    //only one logger exist (Singlton)
    private Logger() {}

    // Returns the single shared Logger instance
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }
    // Prints a log message to the console
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
    
}
