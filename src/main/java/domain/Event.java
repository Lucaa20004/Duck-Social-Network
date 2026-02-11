package domain;

import java.util.ArrayList;
import java.util.List;

public class Event implements Entity<Long>{
    private Long id;
    private String nume;
    private final List<User> subscribers;

    public Event(Long id, String nume) {
        this.id = id;
        this.nume = nume;
        this.subscribers = new ArrayList<>();
    }

    public void subscribe(User user) {

        if(!subscribers.contains(user)) {
            subscribers.add(user);
            System.out.println(user.getUsername() + " s a abonat la evenioment" + nume);
        }
    }

    public void unsubscribe(User user) {
        if(subscribers.contains(user)) {
            subscribers.remove(user);
            System.out.println(user.getUsername() + "s a dezabonat de la evenioment" + nume);
        }
    }

    public void notifySubscribers(String message) {
        System.out.println("--- Notificare Eveniment: " + nume + " ---");
        System.out.println(message);
        System.out.println("----------------------------------------");
        for (User user : subscribers) {
            // Aici, într-o aplicație completă, ai apela o metodă pe User
            // de ex: user.receiveMessage("Sistem", message);
            user.getMesaje().add(message);
            System.out.println("-> Notificare trimisa catre: " + user.getUsername());
        }
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    @Override
    public Long getId() {
        return id;
    }

    // Adaugă @Override aici
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public List<User> getSubscribers() {
        return subscribers;
    }
}
