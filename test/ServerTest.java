import model.Member;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    // Test 1: Check Member to save data rightly
    @Test
    void testMemberCreation() {
        Member member = new Member("Anna", "127.0.0.1", 1234);

        assertEquals("Anna", member.getId());
        assertEquals("127.0.0.1", member.getIp());
        assertEquals(1234, member.getPort());
        assertFalse(member.isCoordinator());
    }

    // Test 2: Coordinator assignment
    @Test
    void testCoordinatorAssignment() {
        Member member = new Member("Kateryna", "127.0.0.1", 5001);
        assertFalse(member.isCoordinator());

        member.setCoordinator(true);
        assertTrue(member.isCoordinator());

        member.setCoordinator(false);
        assertFalse(member.isCoordinator());
    }
}