package client;

import util.Logger;

import javax.imageio.plugins.tiff.TIFFImageReadParam;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

//Main client class - connects to server and handles user input
public class Client {
    private final String clientId;
    private final String serverIp;
    private final int serverPort;

    private Socket socket;
    private BufferedWriter writer;
    private boolean running = true;

    public Client(String clientId, String serverIp, int serverPort) {
        this.clientId = clientId;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void start() {
        try {
            // Connect to server
            socket = new Socket(serverIp, serverPort);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //send ID as first message
            writer.write(clientId + "\n");
            writer.flush();

            Logger.getInstance().log("Connected to server: "
                    + serverIp + ":" + serverPort);
            printHelp();

            //Start listener thread for incoming messages
            Thread listenerThread = new Thread(new MessageListener(socket, clientId, writer));
            listenerThread.setDaemon(true); //stops when main thread stops
            listenerThread.start();

            //read user input
            handleUserInput();
        } catch (IOException e) {
            Logger.getInstance().log("Cannot connect to server" + e.getMessage());
        }
    }
    // Handles commands typed by user
    private void handleUserInput() {
        Scanner scanner = new Scanner(System.in);

        while (running && scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("/quit")) {
                // Disconnect gracefully
                disconnect();
                break;

            } else if (input.equalsIgnoreCase("/list")) {
                // Request member list
                sendRaw("LIST|" + clientId + "|SERVER|list");

            } else if (input.startsWith("/msg ")) {
                // Private message: /msg TargetID text
                String[] parts = input.split(" ", 3);
                if (parts.length < 3) {
                    System.out.println("Usage /msg <ID> <text>");
                } else {
                    String toId = parts[1];
                    String text = parts[2];
                    sendRaw("PRIVATE|" + clientId + "|" + toId + "|" + text);
                }

            } else {
                // Default: broadcast message to everyone
                sendRaw("BROADCAST|" + clientId + "|null|" + input);
            }
        }
    }

    // Sends a raw formatted message to server
    public void sendRaw(String rawMessage) {
        try {
            writer.write(rawMessage + "\n");
            writer.flush();
        } catch (IOException e) {
            Logger.getInstance().log("Failed to send: "
                    + e.getMessage());
        }
    }

    // Prints available commands to user
    private void printHelp() {
        System.out.println("/list           — show all members");
        System.out.println("/msg <ID> <text> — private message");
        System.out.println("/quit           — disconnect");
        System.out.println("<text>          — broadcast to all");
    }

    private void disconnect() {
        running = false;
        try {
            socket.close();
        } catch (IOException e) {
            Logger.getInstance().log("Error disconnecting");
        }
    }

    // Entry point — accepts args: clientId [serverIp] [port]
    public static void main(String[] args) {
        String id = args.length > 0 ? args[0] : "Client1";
        String ip = args.length > 1 ? args[1] : "localhost";
        int port  = args.length > 2 ? Integer.parseInt(args[2]) : 1234;

        new Client(id, ip, port).start();
    }
}