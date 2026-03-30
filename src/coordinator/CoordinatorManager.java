package coordinator;

import server.Server;
import util.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Manages coordinator logic and periodic ping to all clients
public class CoordinatorManager {
    // Ping interval in seconds
    private static final int PING_INTERVAL = 20;
    private static final int TIMEOUT = 40;

    private final Server server;
    private final ScheduledExecutorService scheduler;

    //Singleton instance
    private static CoordinatorManager instance;

    private CoordinatorManager(Server server) {
        this.server = server;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static CoordinatorManager getInstance(Server server) {
        if (instance == null) {
            instance = new CoordinatorManager(server);
        }
        return instance;
    }
    // Starts periodic ping to all clients
    public void startPing() {
        scheduler.scheduleAtFixedRate(() -> {
            // Send PING to each client
            server.pingAllClients();
            // Check timeouts
            server.checkClientTimeouts(TIMEOUT);
        }, PING_INTERVAL, PING_INTERVAL, TimeUnit.SECONDS);

        Logger.getInstance().log("Ping started every " + PING_INTERVAL + " seconds");
    }
}
