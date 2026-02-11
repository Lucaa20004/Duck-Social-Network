package repository;

import exeption.UserNotFoundExeption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FriendshipRepository {

    private final Map<Long, Set<Long>> friendships;

    public FriendshipRepository() {
        this.friendships = new HashMap<>();
    }

    public void save(Long userId) {
        friendships.put(userId, new HashSet<>());
    }

    public void delete(Long userId) {
        friendships.remove(userId);
        for (Set<Long> friendList : friendships.values()) {
            friendList.remove(userId);
        }
    }

    public void addFriendship(Long userId1, Long userId2) {
        // 1. Verificăm dacă userul 1 are o listă de prieteni. Dacă nu, o creăm.
        if (!friendships.containsKey(userId1)) {
            friendships.put(userId1, new HashSet<>());
        }

        // 2. Verificăm dacă userul 2 are o listă de prieteni. Dacă nu, o creăm.
        if (!friendships.containsKey(userId2)) {
            friendships.put(userId2, new HashSet<>());
        }

        // 3. Acum putem adăuga prietenia în siguranță
        friendships.get(userId1).add(userId2);
        friendships.get(userId2).add(userId1);

        // (Opțional) Mesajul de succes e gestionat în Service, deci nu e nevoie de print aici
    }

    public void removeFriendship(Long userId1, Long userId2) {
        friendships.get(userId1).remove(userId2);
        friendships.get(userId2).remove(userId1);
    }

    public Map<Long, Set<Long>> getFriendships() {
        return friendships;
    }

    public boolean friendshipExists(Long userId1, Long userId2) {
        if (friendships.containsKey(userId1)) {
            return friendships.get(userId1).contains(userId2);
        }
        return false;
    }
}