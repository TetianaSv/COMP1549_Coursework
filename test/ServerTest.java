import command.*;
import org.junit.jupiter.api.Test;
import server.Server;
import coordinator.CoordinatorManager;


import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {


    @Test
    void createsListCommand() {
        CommandFactory factory = new CommandFactory(Server.getInstance());

        String raw = "LIST|Anna|null|list";

        Command command = factory.createCommand(raw, null, "Anna");

        assertTrue(command instanceof ListCommand);
    }

    @Test
    void createsPrivateCommand() {
        CommandFactory factory = new CommandFactory(Server.getInstance());

        String raw = "PRIVATE|Anna|Kateryna|Hello";

        Command command = factory.createCommand(raw, null, "Anna");

        assertTrue(command instanceof PrivateCommand);
    }

    @Test
    void createsBroadcastCommand() {
        CommandFactory factory = new CommandFactory(Server.getInstance());

        Command command = factory.createCommand(
                "BROADCAST|Anna|null|Hello everyone",
                null,
                "Anna"
        );

        assertTrue(command instanceof BroadcastCommand);
    }
}

class CoordinatorManagerTest {

    @Test
    void returnsSameInstance() {
        Server server = Server.getInstance();
        server.clearClients();

        CoordinatorManager manager1 = CoordinatorManager.getInstance(server);
        CoordinatorManager manager2 = CoordinatorManager.getInstance(server);

        assertSame(manager1, manager2);
    }

}

