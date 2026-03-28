package model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {

    public enum Type {
        BROADCAST, //to everyone
        PRIVATE, //to someone
        SYSTEM //service (connected, disconnected, coordinator)
    }

    private final Type type;
    private final String fromId;
    private final String toId;  //null if it is to broadcast or system
    private final String text;
    private final String timestamp;

    public Message (Type type, String fromId, String toId, String text) {
        this.type = type;
        this.fromId = fromId;
        this.toId = toId;
        this.text = text;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public Type getType() { return type; }
    public String getFromId() { return fromId; }
    public String getToId() { return toId; }
    public String getText() { return text; }
    public String getTimestamp() { return timestamp; }

    public String serialize() {
        return type + "|" + fromId + "|" + (toId != null ? toId : "null") +"|" + text;
    }

    public static Message deserialize(String raw) {
        String[] parts = raw.split("\\|", 4);
        Type type = Type.valueOf(parts[0]);
        String from = parts[1];
        String to = parts[2].equals("null") ? null : parts[2];
        String text = parts[3];
        return new Message(type, from, to, text);
    }

    @Override
    public String toString() {
        return "[" + timestamp + "]" + fromId + "->" + (toId != null ? toId : "ALL") + ": " + text;
    }
}
