package model;

public class Member {
    private final String id;
    private final String ip;
    private final int port;
    private boolean isCoordinator;

    public Member (String id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.isCoordinator = false;
    }

    public String getId() { return id; }
    public String getIp() { return ip; }
    public int getPort() {return port; }
    public boolean isCoordinator() { return isCoordinator; }

    public void setCoordinator(boolean coordinator) {
        isCoordinator = coordinator;
    }
    @Override
    public String toString() {
        return "[" +id + "|" + ip + ":" + port +(isCoordinator ? "| COORDINATOR" : "") + "]";
    }
}
