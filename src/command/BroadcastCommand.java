package command;

import server.Server;
import util.Logger;

//Send message for all clients
public class BroadcastCommand implements Command {

    private final Server server;
    private final String rawMessage;
    private final String fromId;

    public BroadcastCommand(Server server, String rawMessage, String fromId) {
        this.server = server;
        this.rawMessage = rawMessage;
        this.fromId = fromId;
    }

    @Override
    public void execute() {
        //send everyone except sender
        server.broadcast(rawMessage, fromId);
        Logger.getInstance().logSystem("Broadcast from" + fromId);
    }
}
