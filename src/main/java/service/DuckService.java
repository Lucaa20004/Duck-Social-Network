package service;

import domain.*;
import event.EntityChangeEvent;
import event.EntityChangeEventType;
import observer.Observable;
import observer.Observer;
import paging.Page;
import paging.Pageable;
import repository.*;
import repository.CardDBRepository;
import repository.EventDBRepository;
import repository.FriendshipDBRepository;
import repository.InMemoryRepository; // Păstrăm importul pentru getEntitiesMap
import exeption.UserNotFoundExeption;
import exeption.ValidationExeption;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
public class DuckService implements Observable<EntityChangeEvent> {

    private final Repository<Long, User> userRepo;
    private final FriendshipDBRepository friendRepo;
    private final Repository<Long, Card> cardRepo;
    private final EventDBRepository eventRepo;
    private final Repository<Long, Message> messageRepo;
    private final Repository<Long, FriendRequest> requestRepo;
    private final SystemNotificationDBRepository notificationRepo;    private List<Observer<EntityChangeEvent>> observers = new ArrayList<>();

    public DuckService(Repository<Long, User> userRepo,
                       FriendshipDBRepository friendRepo,
                       Repository<Long, Card> cardRepo,
                       EventDBRepository eventRepo,
                       Repository<Long, Message> messageRepo,
                       Repository<Long, FriendRequest> requestRepo,
                       SystemNotificationDBRepository notificationRepo) { // Parametru nou
        this.userRepo = userRepo;
        this.friendRepo = friendRepo;
        this.cardRepo = cardRepo;
        this.eventRepo = eventRepo;
        this.messageRepo = messageRepo; // Atribuire nouă
        this.requestRepo = requestRepo;
        this.notificationRepo = notificationRepo;
    }


    @Override
    public void addObserver(Observer<EntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EntityChangeEvent t) {
        for (Observer<EntityChangeEvent> observer : observers) {
            observer.update(t);
        }
    }

    public void addUser(User user) throws ValidationExeption {
        User saved = userRepo.save(user);
        if (saved != null) {
            throw new ValidationExeption("Utilizatorul cu acest ID există deja sau eroare la salvare!");
        }
        friendRepo.save(user.getId());

        notifyObservers(new EntityChangeEvent(EntityChangeEventType.ADD, user));
    }

    public void removeUser(Long userId) throws UserNotFoundExeption {
        if (userRepo.findOne(userId) == null) {
            throw new UserNotFoundExeption("Utilizatorul nu există!");
        }
        userRepo.delete(userId);
        friendRepo.delete(userId); // Șterge legăturile din DB/memorie
        System.out.println("Utilizator șters.");

        notifyObservers(new EntityChangeEvent(EntityChangeEventType.DELETE, userId));
    }



    public void addFriendship(Long userId1, Long userId2) throws UserNotFoundExeption {
        if (userRepo.findOne(userId1) == null || userRepo.findOne(userId2) == null) {
            throw new UserNotFoundExeption("Utilizatorii trebuie sa existe!");
        }
        friendRepo.addFriendship(userId1, userId2); // Face INSERT în DB
        System.out.println("Prietenii actualizate.");
    }

    public void removeFriendship(Long userId1, Long userId2) throws UserNotFoundExeption {
        if (userRepo.findOne(userId1) == null || userRepo.findOne(userId2) == null) {
            throw new UserNotFoundExeption("Utilizatorii trebuie să existe!");
        }
        friendRepo.removeFriendship(userId1, userId2); // Face DELETE în DB
        System.out.println("Prietenie stearsa.");
    }



    public void addCard(Card card) {
        cardRepo.save(card);
        System.out.println("Card adaugat: " + card.getNumeCard());
    }


    public void addToCard(Long cardId, Long userId) throws Exception {
        User user = userRepo.findOne(userId);
        if (user == null || !(user instanceof Duck)) {
            throw new UserNotFoundExeption("ID-ul nu apartine unei rate.");
        }
        Card card = cardRepo.findOne(cardId);
        if (card == null) {
            throw new Exception("Cardul nu exista.");
        }

        Duck duckToAdd = (Duck) user;


        card.addMembru(duckToAdd);

        if (cardRepo instanceof CardDBRepository) {
            ((CardDBRepository) cardRepo).addMemberToCard(cardId, userId);
        }

        System.out.println("Succes! Rata " + duckToAdd.getUsername() + " a fost adaugata în card.");
    }


    public void afiseazaCard() {
        Iterable<Card> cards = cardRepo.findAll();

        if (cardRepo instanceof CardDBRepository) {
            Map<Long, List<Long>> memberships = ((CardDBRepository) cardRepo).loadAllMemberships();

            for (Card c : cards) {
                List<Long> memberIds = memberships.get(c.getId());

                if (c.getMembri() != null) { c.getMembri().clear(); }

                if (memberIds != null) {
                    for (Long userId : memberIds) {
                        User u = userRepo.findOne(userId);
                        if (u instanceof Duck) {
                            try {
                                c.addMembru((Duck) u);
                            } catch (Exception ignored) { }
                        }
                    }
                }
            }
        }

        int count = 0; for (Card c : cards) count++;
        System.out.println("--- Carduri (" + count + ") ---");
        for (Card card : cards) {
            System.out.println("Card ID: " + card.getId() + ", Nume: " + card.getNumeCard());
            Map<Long, Duck> membri = card.getMembri();
            if (membri.isEmpty()) {
                System.out.println("  Membri: Niciunul");
            } else {
                System.out.println("  Membri (" + membri.size() + "):");
                membri.values().forEach(duck -> {
                    System.out.println("    - " + duck.getUsername() + " (ID: " + duck.getId() + ")");
                });
            }
            System.out.println();
        }
    }


    public void saveEvent(Event event) {
        eventRepo.save(event);
        System.out.println("Eveniment adaugat: " + event.getNume());
    }

    public void runRaceEvent(Long eventId) throws Exception {
        Event event = eventRepo.findOne(eventId);
        if (event == null) {
            throw new Exception("Evenimentul nu a fost gasit.");
        }
        if (!(event instanceof RaceEvent)) {
            throw new Exception("Evenimentul nu este o cursa.");
        }

        String mesajStart = "A inceput cursa: " + event.getNume();
        event.notifySubscribers(mesajStart);

        Map<Long, User> allUsers = getEntitiesMap(userRepo);

        System.out.println("--- Se porneste cursa: " + event.getNume() + " ---");
        ((RaceEvent) event).runRace(allUsers);
        System.out.println("--- Cursa s-a incheiat ---");

        String mesajStop = "Cursa '" + event.getNume() + "' s-a terminat.";
        event.notifySubscribers(mesajStop);
    }

    public void runRaceEventConcurren(Long eventId) throws Exception {
        Event event = eventRepo.findOne(eventId);
        if (event == null) throw new Exception("Evenimentul nu a fost gasit.");

        if(!(event instanceof RaceEvent)) {
            throw new Exception("Evenimentul nu este o cursa.");
        }

        List<Long> subscribersIds = new ArrayList<>();
        if(eventRepo instanceof EventDBRepository) {
            subscribersIds = ((EventDBRepository) eventRepo).getSubscribers(eventId);
        }
        final List<Long> finalSubscribersIds = subscribersIds;

        Map<Long, User> allUsers = getEntitiesMap(userRepo);

        new Thread(() -> {
            try {
                String startMsg = "A incpeut cursa ";
                sendSystemNotificationToUsers(finalSubscribersIds, startMsg);
                notifyObservers(new EntityChangeEvent(EntityChangeEventType.UPDATE, null));

                Thread.sleep(300);

                String rezultatFinal = ((RaceEvent) event).runRace(allUsers);

                System.out.println(rezultatFinal); // Vedem și în consolă

                // D. Notificăm FINALUL cu Rezultatele reale
                sendSystemNotificationToUsers(finalSubscribersIds, rezultatFinal);

                // E. Actualizăm GUI
                notifyObservers(new EntityChangeEvent(EntityChangeEventType.UPDATE, event));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void subscribeToEvent(Long userID , Long eventID) throws Exception {
        User user = userRepo.findOne(userID);
        if (user == null) {
            throw new Exception("Nu exista utilizatorul cu acest ID");
        }
        Event event = eventRepo.findOne(eventID);
        if (event == null) {
            throw new Exception("Evenimentul nu a fost gasit");
        }

        event.subscribe(user);
        eventRepo.addSubscriber(eventID,userID);
        String mesajNotificare = "Ati fost abonat cu succes la evenimentul '" + event.getNume() + "'.";

        if(user.getMesaje() != null) {
            user.getMesaje().add(mesajNotificare);
        }
        System.out.println("Utilizatorul a fost notificat (mesaj adaugat in lista sa).");
    }

    public void unsubscribeRoEvent(Long userID , Long eventID) throws Exception {
        User user = userRepo.findOne(userID);
        if (user == null) {
            throw new Exception("Nu există utilizatorul cu acest ID");
        }
        Event event = eventRepo.findOne(eventID);
        if (event == null) {
            throw new Exception("Evenimentul nu a fost găsit");
        }

        event.unsubscribe(user);
        String mesajNotificare = "Ati fost dezabonat cu succes de la evenimentul '" + event.getNume() + "'.";

        if(user.getMesaje() != null) {
            user.getMesaje().add(mesajNotificare);
        }
        System.out.println("Utilizatorul a fost notificat.");
    }

    public void afiseazaSubscriber(Long eventID) {
        Event event = eventRepo.findOne(eventID);
        if (event == null) {
            System.out.println("Evenimentul nu exista.");
            return;
        }

        List<User> subscribers = event.getSubscribers();
        if (subscribers.isEmpty()) {
            System.out.println("Acest eveniment nu are niciun abonat.");
        } else {
            System.out.println("--- Abonati la " + event.getNume() + " ---");
            for (User user : subscribers) {
                System.out.println("- " + user.getUsername() + " (ID: " + user.getId() + ")");
            }
        }
    }


    public Map<Long, Set<Long>> getFriendships() {
        return friendRepo.getFriendships();
    }


    public int getCommunityCount() {
        Map<Long, User> users = getEntitiesMap(userRepo);
        Set<Long> visited = new HashSet<>();
        int count = 0;
        for (Long userId : users.keySet()) {
            if (!visited.contains(userId)) {
                count++;
                dfs(userId, visited);
            }
        }
        return count;
    }

    public Set<User> getMostSociableCommunity() {
        Map<Long, User> users = getEntitiesMap(userRepo);
        List<Set<Long>> components = findAllComponents();
        int maxDiameter = -1;
        Set<Long> bestComponentIds = null;

        for (Set<Long> component : components) {
            int diameter = getComponentDiameter(component);
            if (diameter > maxDiameter) {
                maxDiameter = diameter;
                bestComponentIds = component;
            }
        }

        Set<User> mostSociableCommunity = new HashSet<>();
        if (bestComponentIds != null) {
            for (Long id : bestComponentIds) {
                mostSociableCommunity.add(users.get(id));
            }
        }
        System.out.println("Diametrul maxim gasit: " + maxDiameter);
        return mostSociableCommunity;
    }



    private int getComponentDiameter(Set<Long> component) {
        int maxDiameter = 0;
        for (Long startNode : component) {
            int eccentricity = bfsForEccentricity(startNode, component);
            maxDiameter = Math.max(maxDiameter, eccentricity);
        }
        return maxDiameter;
    }

    private int bfsForEccentricity(Long startNode, Set<Long> component) {
        Map<Long, Set<Long>> friendships = friendRepo.getFriendships();
        Map<Long, Integer> distance = new HashMap<>();
        Queue<Long> queue = new LinkedList<>();

        distance.put(startNode, 0);
        queue.add(startNode);
        int maxDist = 0;

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            if (friendships.get(current) != null) {
                for (Long neighbor : friendships.get(current)) {
                    if (component.contains(neighbor) && !distance.containsKey(neighbor)) {
                        int newDist = distance.get(current) + 1;
                        distance.put(neighbor, newDist);
                        queue.add(neighbor);
                        maxDist = Math.max(maxDist, newDist);
                    }
                }
            }
        }
        return maxDist;
    }

    private List<Set<Long>> findAllComponents() {
        Map<Long, User> users = getEntitiesMap(userRepo);
        List<Set<Long>> components = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        for (Long userId : users.keySet()) {
            if (!visited.contains(userId)) {
                Set<Long> currentComponent = new HashSet<>();
                dfsFindComponent(userId, visited, currentComponent);
                components.add(currentComponent);
            }
        }
        return components;
    }

    private void dfsFindComponent(Long userId, Set<Long> visited, Set<Long> currentComponent) {
        Map<Long, Set<Long>> friendships = friendRepo.getFriendships();
        visited.add(userId);
        currentComponent.add(userId);

        if (friendships.get(userId) != null) {
            for (Long friendId : friendships.get(userId)) {
                if (!visited.contains(friendId)) {
                    dfsFindComponent(friendId, visited, currentComponent);
                }
            }
        }
    }

    private void dfs(Long userId, Set<Long> visited) {
        Map<Long, Set<Long>> friendships = friendRepo.getFriendships();
        visited.add(userId);
        if (friendships.get(userId) != null) {
            for (Long friendId : friendships.get(userId)) {
                if (!visited.contains(friendId)) {
                    dfs(friendId, visited);
                }
            }
        }
    }

    public void afiseazaMesaje(Long userID) {
        User user = userRepo.findOne(userID);
        if (user == null) {
            System.err.println("Eroare: Utilizatorul cu ID " + userID + " nu a fost gasit.");
            return;
        }

        List<String> mesaje = user.getMesaje();
        System.out.println("--- Mesaje pentru: " + user.getUsername() + " ---");

        if (mesaje == null || mesaje.isEmpty()) {
            System.out.println("Utilizatorul nu are mesaje noi.");
        } else {
            for (int i = 0; i < mesaje.size(); i++) {
                System.out.println((i + 1) + ". " + mesaje.get(i));
            }
        }
    }

    public void afiseazaRetea() {
        Iterable<User> users = userRepo.findAll();
        Map<Long, Set<Long>> friendships = friendRepo.getFriendships();

        int count = 0;
        for(User u : users) count++;

        System.out.println("--- Utilizatori (" + count + ") ---");
        users.forEach(System.out::println);

        System.out.println("--- Prietenii ---");
        friendships.forEach((userId, friends) -> {
            User u = userRepo.findOne(userId);
            if(u != null) {
                System.out.print(u.getUsername() + " e prieten cu: ");
                if (friends.isEmpty()) {
                    System.out.println("nimeni");
                } else {
                    friends.forEach(friendId -> {
                        User friend = userRepo.findOne(friendId);
                        if(friend != null) System.out.print(friend.getUsername() + " ");
                    });
                    System.out.println();
                }
            }
        });
        System.out.println("-----------------");
    }

    private <ID, E extends Entity<ID>> Map<ID, E> getEntitiesMap(Repository<ID, E> repo) {
        if (repo instanceof InMemoryRepository) {
            return ((InMemoryRepository<ID, E>) repo).getEntities();
        }
        Map<ID, E> map = new HashMap<>();
        for (E entity : repo.findAll()) {
            map.put(entity.getId(), entity);
        }
        return map;
    }
    public Page<User> findAllOnPage(Pageable pageable, String filterType) {
        if (userRepo instanceof repository.DuckRepository) { // Sau cum ai numit interfața paginată
            return ((repository.DuckRepository) userRepo).findAllOnPage(pageable, filterType);
        }
        throw new UnsupportedOperationException("Acest repository nu suportă paginare.");
    }


    public User login(String username, String password) {
        Iterable<User> users = userRepo.findAll();
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                if (u.getPassword().equals(password)) {
                    return u;
                }
            }
        }
        return null;
    }

    public void sendMessage(Long fromId, Long toId, String text, Message replyTo) {
        User from = userRepo.findOne(fromId);
        User to = userRepo.findOne(toId);

        if (from == null || to == null) {
            throw new RuntimeException("Utilizatorii nu există!");
        }

        Message msg = new Message(null, from, to, text, LocalDateTime.now(), replyTo);

        messageRepo.save(msg);

        notifyObservers(new EntityChangeEvent(EntityChangeEventType.ADD, msg));
    }

    public List<Message> getConversation(Long user1Id, Long user2Id) {
        if (messageRepo instanceof MessageDBRepository) {
            return ((MessageDBRepository) messageRepo).getConversation(user1Id, user2Id);
        }
        System.out.println("DEBUG SERVICE: messageRepo NU este MessageDBRepository!");
        return new ArrayList<>();
    }

    public List<User> getFriends(Long userId) {
        Map<Long, Set<Long>> allFriendships = friendRepo.getFriendships();

        Set<Long> friendIds = allFriendships.get(userId);

        List<User> friendsList = new ArrayList<>();
        if (friendIds != null) {
            for (Long friendId : friendIds) {
                User friend = userRepo.findOne(friendId);
                if (friend != null) {
                    friendsList.add(friend);
                }
            }
        }
        return friendsList;
    }


    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    public void sendFriendRequest(Long fromId, Long toId) throws Exception {
        User from = userRepo.findOne(fromId);
        User to = userRepo.findOne(toId);

        if (from == null || to == null) {
            throw new Exception("Utilizatorul nu exista!");
        }
        if (fromId.equals(toId)) {
            throw new Exception("Nu poti trimite cerere tie insuti!");
        }

        Map<Long, Set<Long>> friendships = friendRepo.getFriendships();
        if (friendships.containsKey(fromId) && friendships.get(fromId).contains(toId)) {
            throw new Exception("Sunteți deja prieteni!");
        }

        Iterable<FriendRequest> allRequests = requestRepo.findAll();
        for (FriendRequest req : allRequests) {
            if (req.getFrom().getId().equals(fromId) && req.getTo().getId().equals(toId)) {
                if (req.getStatus() == RequestType.PENDING) {
                    throw new Exception("Exista deja o cerere in asteptare catre " + to.getUsername());
                }
            }
            if (req.getFrom().getId().equals(toId) && req.getTo().getId().equals(fromId)) {
                if (req.getStatus() == RequestType.PENDING) {
                    throw new Exception("Acest utilizator i ai trimis deja o cerere! Verifica notificarile.");
                }
            }
        }

        FriendRequest request = new FriendRequest(from, to, RequestType.PENDING, LocalDateTime.now());
        requestRepo.save(request);

        notifyObservers(new EntityChangeEvent(EntityChangeEventType.ADD, request));
    }

    public List<FriendRequest> getPendingRequests(Long userId) {
        Iterable<FriendRequest> allRequests = requestRepo.findAll();

        List<FriendRequest> result = new ArrayList<>();

        for (FriendRequest req : allRequests) {
            if (req.getTo().getId().equals(userId) && req.getStatus() == RequestType.PENDING) {
                result.add(req);
            }
        }

        return result;
    }

    public void respondToRequest(Long requestId, String response) throws Exception {
        FriendRequest request = requestRepo.findOne(requestId);

        if (request == null) {
            throw new Exception("Cererea de prietenie nu a fost gasita!");
        }

        RequestType newStatus;
        if ("APPROVE".equalsIgnoreCase(response)) {
            newStatus = RequestType.APPROVED;
        } else if ("REJECT".equalsIgnoreCase(response)) {
            newStatus = RequestType.REJECTED;
        } else {
            throw new Exception("Raspuns invalid! Foloseste APPROVE sau REJECT.");
        }

        request.setStatus(newStatus);
        requestRepo.update(request);

        if (newStatus == RequestType.APPROVED) {
            Long user1Id = request.getFrom().getId();
            Long user2Id = request.getTo().getId();

            friendRepo.addFriendship(user1Id, user2Id);
            System.out.println("Prietenie creata intre " + user1Id + " și " + user2Id);
        }

        notifyObservers(new EntityChangeEvent(EntityChangeEventType.UPDATE, request));
    }

    private void sendSystemNotificationToUsers(List<Long> userIds, String message) {
        for (Long uid : userIds) {
            User u = userRepo.findOne(uid);
            if (u != null) {
                // Creăm notificarea
                SystemNotification note = new SystemNotification(u, message, LocalDateTime.now());

                // O salvăm în Baza de Date
                notificationRepo.save(note);

                System.out.println("Notificare salvată pentru " + u.getUsername());
            }
        }
    }

    public List<SystemNotification> getUserNotifications(Long userId) {
        return notificationRepo.findAllForUser(userId);
    }


    public UserProfilePage getUserProfilePage(Long userId) {
        // 1. Informații User
        User u = userRepo.findOne(userId);

        // 2. Lista de Prieteni
        List<User> friends = getFriends(userId);

        // 3. Notificările/Activitatea recentă (folosim ce am implementat la pasul anterior)
        List<SystemNotification> notifications = getUserNotifications(userId);

        // 4. Returnăm obiectul compus
        return new UserProfilePage(u, friends, notifications);
    }
}