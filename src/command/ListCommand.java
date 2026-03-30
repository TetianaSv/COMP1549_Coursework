package command;

import server.ClientHandler;
import server.Server;

//returns list of all members
public class ListCommand implements Command{

    private final Server server;
    private final ClientHandler requester;
    private final String clientId;

    public ListCommand(Server server, ClientHandler requester, String clientId) {
        this.server = server;
        this.requester = requester;
        this.clientId = clientId;
}

    @Override
    public void execute() {
        String coordinatorId = server.getCoordinatorId();
        // Send member list back to requester
        requester.sendMessage(
                "COORDINATOR|" + coordinatorId + "|" + clientId + "|" + server.getMembersList()
        );
    }
}
