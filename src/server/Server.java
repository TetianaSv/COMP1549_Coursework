package server;

import util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import coordinator.CoordinatorManager;

public class Server {

    private static final int PORT = 1234;
    private static Server instance;
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private String coordinatorId = null;

    private Server() {}

    public static void main(String[] args) {
        Server.getInstance().start();
    }

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public void start() {
        Logger.getInstance().log("Server started on port" + PORT);
        CoordinatorManager.getInstance(this).startPing();

        // Start coordinator ping
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler (socket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            Logger.getInstance().log("System error: " + e.getMessage());
        }
    }

    //Add new client
    public synchronized void addClient(String id, ClientHandler handler) {
        clients.put(id, handler);

        if (coordinatorId == null) {
            //first client become a coordinator
            coordinatorId = id;
            handler.sendMessage("SYSTEM|SERVER|" + id + "|YOU ARE COORDINATOR");
        } else {
            // Tell new client who is coordinator
            handler.sendMessage("SYSTEM|SERVER|" + id + "|Current coordinator is: " + coordinatorId);
        }

        //Announce a new member
        broadcast("SYSTEM|SERVER|null|" + id + " added to the group", null);
    }
    //delete client after log out
    public synchronized void removeClient(String id) {
        clients.remove(id);

        Logger.getInstance().log(id + " logged out");
        broadcast("SYSTEM|SERVER|null|" + id + " logged out", null);

        //If coordinator is gone
        if (id.equals(coordinatorId)) {
            electNewCoordinator();
        }
    }

    //Choose new coordinator
    //fault tolerance
    private void electNewCoordinator() {
        if (clients.isEmpty()) {
            coordinatorId = null;
            Logger.getInstance().log("Group is empty");
            return;
        }

        //First client in list became a coordinator
        String newCoordinatorId = clients.keySet().iterator().next();
        coordinatorId = newCoordinatorId;

        clients.get(newCoordinatorId).sendMessage("SYSTEM|SERVER|NULL|" + newCoordinatorId + " IS NEW COORDINATOR");
        broadcast("SYSTEM|SERVER|null|New coordinator: " + newCoordinatorId, null);
        Logger.getInstance().log("New coordinator: " + newCoordinatorId);
    }

    //Send a message everyone except sender
    public void broadcast(String rawMessage, String excludeId) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equals(excludeId)) {
                entry.getValue().sendMessage(rawMessage);
            }
        }
    }

    //Send private message
    public void sendPrivate(String rawMessage, String toId) {
        ClientHandler handler = clients.get(toId);
        if (handler != null) {
            handler.sendMessage(rawMessage);
        } else {
            Logger.getInstance().log("Client " + toId + "not found");
        }
    }

    //Return list of all clients
    public String getMembersList() {
        StringBuilder sb = new StringBuilder("Members:");
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            String id = entry.getKey();
            ClientHandler handler = entry.getValue();

            sb.append("\\n- ").append(id)
                    .append(" ").append(handler.getClientIp())
                    .append(":").append(handler.getClientPort());

            if (id.equals(coordinatorId)) {
                sb.append(" [COORDINATOR]");
            }
        }
        return sb.toString();
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }

    // Send PING to all clients
    public void pingAllClients() {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            entry.getValue().sendMessage(
                    "PING|SERVER|" + entry.getKey() + "|ping"
            );
        }
    }

    // Check if any client timed out
    public void checkClientTimeouts(int timeoutSeconds) {
        long now = System.currentTimeMillis();
        long timeoutMs = timeoutSeconds * 1000L;

        // collect timed out clients first
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            ClientHandler handler = entry.getValue();
            long timeSinceLastPong = now - handler.getLastPongTime();

            if (timeSinceLastPong > timeoutMs) {
                Logger.getInstance().log(entry.getKey() + " timed out");
                removeClient(entry.getKey());
            }
        }
    }
    public boolean hasClient(String clientId) {
        return clients.containsKey(clientId);
    }
}
