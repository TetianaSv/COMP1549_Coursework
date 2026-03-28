package command;

import server.ClientHandler;
import server.Server;

// Creates the correct command based on message type
public class CommandFactory {
    private final Server server;

    public CommandFactory(Server server) {
        this.server = server;
    }
    public Command createCommand(String rawMessage, ClientHandler handler, String clientId) {
        // Expected format: TYPE|FROM|TO|TEXT
        String[] parts = rawMessage.split("\\|", 4);

        if (parts.length < 4) {
            // Return empty command for invalid messages
            // Повертаємо порожню команду для невірних повідомлень
            return () -> handler.sendMessage(
                    "SYSTEM|SERVER|" + clientId + "|Invalid message format"
            );
        }
        String type = parts[0];
        String fromId = parts[1];
        String toId = parts[2];

        switch (type) {
            case "BROADCAST":
                return new BroadcastCommand(server, rawMessage, fromId);
            case "PRIVATE":
                return new PrivateCommand(server, rawMessage, fromId, toId);
            case "LIST":
                return new ListCommand(server, handler, clientId);
            default:
                // Unknown command / Невідома команда
                return () -> handler.sendMessage(
                        "SYSTEM|SERVER|" + clientId + "|Unknown command: " + type
                );
        }
    }
}
