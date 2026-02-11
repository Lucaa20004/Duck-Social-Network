package repository;

import domain.Card;
import domain.Duck;
import domain.Card;
import domain.Person;
import domain.User;
import domain.*;
import exeption.UserNotFoundExeption;
import exeption.ValidationExeption;
import validation.DuckValidator;
import validation.PersonValidator;
import validation.Validator;

import java.util.*;


public class NetworkManager {


    private final Map<Long, User> users;
    private final Map<Long, Set<Long>> friendships; // Lista de adiacență a grafului
    private final Map<Long, Card> carduri;
    private final Map<Long, Event> evenimente;

    private final Validator<Person> personValidator;
    private final Validator<Duck> duckValidator;


    private long nextId = 1;
    private long nextCardId = 1;
    private long nextEventId = 1;

    public NetworkManager() {
        this.users = new HashMap<>();
        this.friendships = new HashMap<>();
        this.carduri = new HashMap<>();
        this.personValidator = new PersonValidator();
        this.duckValidator = new DuckValidator();
        this.evenimente = new HashMap<>();
    }


    public void addUser(User user) throws ValidationExeption {
        if (user instanceof Person) {
            personValidator.validate((Person) user);
        } else if (user instanceof Duck) {
            duckValidator.validate((Duck) user);
        }

        if (user.getId() == null || user.getId() == 0) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        friendships.put(user.getId(), new HashSet<>());
        System.out.println("Utilizator adaugat: " + user);
    }

    public void addCard(Card card) {
        if (card.getId() == null || card.getId() == 0) {
            card.setId(nextCardId++);
        }
        carduri.put(card.getId(), card);
        System.out.println("Card adaugat: " + card);
    }

    public void saveEvent(Event event) {
        if (event.getId() == null || event.getId() == 0) {
            event.setId(nextEventId++); // Presupunând că Event are setId()
        }
        evenimente.put(event.getId(), event);
        System.out.println("Eveniment adaugat: " + event);
    }

    public void addToCard(Long cardId, Long userId) throws Exception {


        User user = users.get(userId); // Caută userul după ID


        if (user == null) {
            throw new UserNotFoundExeption("Nu exista niciun utilizator cu ID-ul " + userId);
        }
        if (!(user instanceof Duck)) {
            throw new Exception("Utilizatorul cu ID-ul " + userId + " este o Persoana, nu o Rață.");
        }

        Duck duckToAdd = (Duck) user;

        Card card = carduri.get(cardId); // Caută cârdul după ID

        if (card == null) {
            throw new Exception("Nu exista niciun card cu ID-ul " + cardId);
        }

        card.addMembru(duckToAdd);

        System.out.println("Succes! Rata " + duckToAdd.getUsername() + " a fost adaugata in cardul " + card.getNumeCard());

    }

    public void removeUser(Long userId) throws UserNotFoundExeption {
        validateUserExists(userId);

        User user = users.remove(userId);
        friendships.remove(userId);

        for (Set<Long> friendList : friendships.values()) {
            friendList.remove(userId);
        }
        System.out.println("Utilizator șters: " + user);
    }


    public void addFriendship(Long userId1, Long userId2) throws UserNotFoundExeption {
        validateUserExists(userId1);
        validateUserExists(userId2);

        friendships.get(userId1).add(userId2);
        friendships.get(userId2).add(userId1);
        System.out.println("Prietenie adaugata intre " + users.get(userId1).getUsername() + " si " + users.get(userId2).getUsername());
    }


    public void removeFriendship(Long userId1, Long userId2) throws UserNotFoundExeption {
        validateUserExists(userId1);
        validateUserExists(userId2);

        friendships.get(userId1).remove(userId2);
        friendships.get(userId2).remove(userId1);
        System.out.println("Prietenie stearsa intre " + users.get(userId1).getUsername() + " și " + users.get(userId2).getUsername());
    }

    public Map<Long, User> getusers() {
        return users;
    }
    public Map<Long, Set<Long>> getfriendships() {
        return friendships;
    }
    public Map<Long, Event> getEvenimente() {
        return evenimente;
    }
    public  Map<Long, Card> getcarduri() {return  carduri;}
    private void validateUserExists(Long userId) throws UserNotFoundExeption {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundExeption("Utilizatorul cu ID-ul " + userId + " nu a fost gasit!");
        }
    }


}