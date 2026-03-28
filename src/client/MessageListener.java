package client;

import util.Logger;

import java.io.*;
import java.net.Socket;

// Listens for incoming messages from server in a separate thread
public class MessageListener implements Runnable {

    private final Socket socket;
    private final String clientId;

    public MessageListener(Socket socket, String clientId) {
        this.socket = socket;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            String rawMessage;

            //Keep reading until connection is closed
            while ((rawMessage = reader.readLine()) != null) {
                displayMessage(rawMessage);
            }
        } catch (IOException e) {
            // Connection was closed — normal when /quit is used
            Logger.getInstance().logSystem(
                    "Disconnected from server"
            );
        }
    }
        // Parses and displays message in a readable format
        private void displayMessage (String rawMessage) {
            // Expected format: TYPE|FROM|TO|TEXT
            String[] parts = rawMessage.split("\\|", 4);

            if (parts.length < 4) {
                System.out.println("[?] " + rawMessage);
                return;
            }

            String type = parts[0];
            String from = parts[1];
            String text = parts[3];

            switch (type) {
                case "BROADCAST":
                    // Message to everyone
                    System.out.println("\n[ALL] " + from + ": " + text);
                    break;

                case "PRIVATE":
                    // Private message
                    System.out.println("\n[PRIVATE] " + from + " → " + clientId + ": " + text);
                    break;

                case "SYSTEM":
                    // System notification
                    String displayed = text.replace("| - ", "\n  - ");
                    System.out.println("\n[SYSTEM] " + displayed);
                    break;

                case "PING":
                    System.out.println("\n[PING] " + text);
                    break;

                default:
                    System.out.println("\n[?] " + rawMessage);
            }
        }
    }
