package coordinator;

import server.Server;
import util.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Manages coordinator logic and periodic ping to all clients
public class CoordinatorManager {
    // Ping interval in seconds
    private static final int PING_INTERVAL = 60;

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
            String coordinatorId = server.getCoordinatorId();

            if (coordinatorId == null) {
                Logger.getInstance().logSystem(
                        "No coordinator — skipping ping"
                );
                return;
            }

            Logger.getInstance().logSystem(
                    "Coordinator ping: " + coordinatorId
            );

            // Broadcast ping to all clients
            server.broadcast(
                    "PING|SYSTEM|null|Coordinator is: " + coordinatorId,
                    null
            );

        }, PING_INTERVAL, PING_INTERVAL, TimeUnit.SECONDS);

        Logger.getInstance().logSystem("Ping started every " + PING_INTERVAL + " seconds");
    }

    // Stops the ping scheduler
    public void stopPing() {
        scheduler.shutdown();
        Logger.getInstance().logSystem("Ping stopped");
    }

    // Скидаємо singleton при перезапуску
    public static void resetInstance() {
        if (instance != null) {
            instance.stopPing();
            instance = null;
        }
    }
}
