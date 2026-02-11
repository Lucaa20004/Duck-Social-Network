package domain;

import java.time.LocalDateTime;

public class FriendRequest implements Entity<Long>{
    private Long id;
    private User to;
    private User from;
    private RequestType status;
    private LocalDateTime date;


    public FriendRequest(User from, User to, RequestType status, LocalDateTime date) {
        this.to = to;
        this.from = from;
        this.status = status;
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

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public RequestType getStatus() {
        return status;
    }

    public void setStatus(RequestType status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
