package server;

import command.Command;
import command.CommandFactory;
import util.Logger;

import java.io.*;
import java.net.Socket;

//Handles communication with a one single connected client
public class ClientHandler implements Runnable {

    private final CommandFactory commandFactory;
    private final Socket socket;
    private final Server server;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientId;
    private volatile long lastPongTime = System.currentTimeMillis();

    //Constructor: initializes handler
    public ClientHandler (Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.commandFactory = new CommandFactory(server);
    }

    // Constructor: initializes handler
    @Override
    public void run() {
        try {
            //Initialize input/output stream
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //first message from client is their ID
            clientId = reader.readLine();

            if (clientId == null || clientId.isBlank()) {
                socket.close();
                return;
            }

            //check for uniq ID
            if (server.hasClient(clientId.trim())) {
                sendMessage("SYSTEM|SERVER|" + clientId + "|ERROR: ID already taken");
                socket.close();
                return;
            }
            //register client on a server
            server.addClient(clientId, this);

            //Listen for incoming message in a loop
            String rawMessage;
            while ((rawMessage = reader.readLine()) != null) {
                handleMessage(rawMessage);
            }
        } catch (IOException e) {
            Logger.getInstance().log("Connection lost with: " + clientId);
        } finally {
            //Clean up when client disconnected
            disconnect();
        }
    }
    //Parses incoming message and routes it to the appropriate command
    private void handleMessage(String rawMessage) {

        if (rawMessage.startsWith("PONG")) {
            updatePongTime();
            return;
        }
        // Create and execute command
        Command command = commandFactory.createCommand(rawMessage, this, clientId);
        command.execute();
    }

    // Sends a raw message to this client
    public void sendMessage(String rawMessage) {
        try {
            writer.write(rawMessage + "\n");
            writer.flush();
        } catch (IOException e) {
            Logger.getInstance().log(
                    "Failed to send to: " + clientId);
        }
    }
    //Returns client's IP address
    public String getClientIp() {
            return socket.getInetAddress().getHostAddress();
    }

    //Returns client's port number
    public int getClientPort() {
        return socket.getPort();
    }

    //Updates last received PONG timestamp
    public void updatePongTime() {
        lastPongTime = System.currentTimeMillis();
    }

    // Returns last time PONG was received from client
    public long getLastPongTime() {
        return lastPongTime;
    }

    //Safely disconnects client
    private void disconnect() {
        try {
            if (clientId != null) {
                server.removeClient(clientId);
            }
            socket.close();
        } catch (IOException e) {
            Logger.getInstance().log("Error closing socket");
        }
    }
}
