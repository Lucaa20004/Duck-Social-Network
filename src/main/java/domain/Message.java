package domain;

import java.time.LocalDateTime;

public class Message implements Entity<Long> {

    private Long id;

    private User from;
    private User to;
    private String message;
    private LocalDateTime date;
    private Message reply;

    public Message(Long id, User from, User to, String message, LocalDateTime date, Message reply) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }


    public User getFrom() { return from; }
    public User getTo() { return to; }
    public String getMessage() { return message; }
    public LocalDateTime getDate() { return date; }
    public Message getReply() { return reply; }

    @Override
    public String toString() {
        String senderName = (from != null) ? from.getUsername() : "Unknown";
        return senderName + ": " + message;
    }
}