package command;

import server.Server;

//sends a message to a specific client
public class PrivateCommand implements Command{

    private final Server server;
    private final String rawMessage;
    private final String toId;

    public PrivateCommand(Server server, String rawMessage, String fromId, String toId) {
        this.server = server;
        this.rawMessage = rawMessage;
        this.toId = toId;
    }

    @Override
    public void execute() {
        //sent to specific client
        server.sendPrivate(rawMessage, toId);

    }
}

