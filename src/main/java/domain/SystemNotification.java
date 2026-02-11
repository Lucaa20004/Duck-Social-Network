package domain;

import java.time.LocalDateTime;

public class SystemNotification implements Entity<Long> {

    private Long id;

    private User user; // Destinatarul
    private String message;
    private LocalDateTime date;

    public SystemNotification(User user, String message, LocalDateTime date) {
        this.user = user;
        this.message = message;
        this.date = date;
    }

    public SystemNotification(Long id, User user, String message, LocalDateTime date) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.date = date;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    // ---------------------------------------

    public User getUser() { return user; }
    public String getMessage() { return message; }
    public LocalDateTime getDate() { return date; }

    @Override
    public String toString() {
        return "[" + date.toLocalDate() + " " + date.toLocalTime().getHour() + ":" + date.toLocalTime().getMinute() + "] " + message;
    }
}