package domain;

import domain.SystemNotification;
import domain.User;
import java.util.List;

public class UserProfilePage {
    private User user;
    private List<User> friendsList;
    private List<SystemNotification> recentNotifications;

    public UserProfilePage(User user, List<User> friendsList, List<SystemNotification> recentNotifications) {
        this.user = user;
        this.friendsList = friendsList;
        this.recentNotifications = recentNotifications;
    }

    public User getUser() { return user; }
    public List<User> getFriendsList() { return friendsList; }
    public List<SystemNotification> getRecentNotifications() { return recentNotifications; }
}