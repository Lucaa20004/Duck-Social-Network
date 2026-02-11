package org.example;

import domain.*;
import exeption.UserNotFoundExeption;
import exeption.ValidationExeption;
import repository.NetworkManager;
import repository.*;
import ui.ConsoleUI;
import validation.*;
import service.DuckService;
import java.time.LocalDate;
import java.util.*;
import javafx.event.ActionEvent;


public class Main {

    public static void main(String[] args) {

        String dbUrl = "jdbc:postgresql://localhost:5432/Duck";
        String dbUser = "postgres";
        String dbPass = "kapy2019"; // Verifică parola ta

        Validator<User> userValidator = new UserValidator();
        Validator<Card> cardValidator = new CardValidator();
        Validator<Event> eventValidator = new EventValidator();


        Repository<Long, User> userRepo = new UserDBRepository(dbUrl, dbUser, dbPass, userValidator);

        Repository<Long, Card> cardRepo = new CardDBRepository(dbUrl, dbUser, dbPass, cardValidator);

        FriendshipDBRepository friendRepo = new FriendshipDBRepository(dbUrl, dbUser, dbPass);

        EventDBRepository eventRepo = new EventDBRepository(dbUrl, dbUser, dbPass, eventValidator);

        Repository<Long, Message> messageRepo = new MessageDBRepository(dbUrl, dbUser, dbPass, userRepo);

        Repository<Long , FriendRequest> requestRepo = new FriendRequestDBRepository(dbUrl, dbUser, dbPass, userRepo);

        SystemNotificationDBRepository systemnotif = new SystemNotificationDBRepository(dbUrl, dbUser, dbPass, userRepo);

        DuckService service = new DuckService(userRepo, friendRepo, cardRepo, eventRepo, messageRepo,  requestRepo,systemnotif);

        DuckApplication.setDuckService(service);
        javafx.application.Application.launch(DuckApplication.class, args);
    }


    private static void loadInitialData(DuckService network) {
        System.out.println("Se incarca datele...");

        try {

            try {
//                network.addUser(new Person(null, "jdoe", "j@d.com", "123", "John", "Doe", LocalDate.now(), "Inginer", 5)); // ID 1
//                network.addUser(new Person(null, "asmith", "a@s.com", "123", "Alice", "Smith", LocalDate.now(), "Doctor", 8)); // ID 2
//                network.addUser(new Person(null, "bwhite", "b@w.com", "123", "Bob", "White", LocalDate.now(), "Student", 3)); // ID 3
//
//                network.addUser(new SwimmingDuck(null, "Daffy", "da@w.com", "qw",  4.0, 2.0)); // ID 4
//                network.addUser(new SwimmingDuck(null, "Daaffy", "ds@w.com", "qe",  5.0, 2.0)); // ID 5
//                network.addUser(new SwimmingDuck(null, "Dacffy", "dd@w.com", "qr",  5.0, 5.0)); // ID 6
//                network.addUser(new SwimmingDuck(null, "Daxffy", "df@w.com", "qtt",  3.0, 5.0)); // ID 7
//                network.addUser(new SwimmingDuck(null, "Dadffy", "dg@w.com", "qy",  2.0, 7.0)); // ID 8
//                //network.addUser(new SwimmingDuck(null, "Donald", "d@d.com", "qu", 9.5, 22.0)); // ID 9

//                network.addUser(new Person(null, "izolat", "i@i.com", "123", "Ion", "Izolatu", LocalDate.now(), "Pustnic", 1)); // ID 10

                network.addFriendship(15L, 16L); // John - Alice
                network.addFriendship(2L, 3L); // Alice - Bob
                network.addFriendship(4L, 5L); // Daffy - Donald
                System.out.println("    - Prietenii incarcate.");
            } catch (Exception e) {
                System.err.println("    ! Avertisment prietenii: " + e.getMessage());
            }

            List<Culoar> list = new ArrayList<>();
//            list.add(new Culoar(3));
//            list.add(new Culoar(6));
//            list.add(new Culoar(10));
//
//            RaceEvent defaultRace = new RaceEvent(null, "Cursa Default", list);
//            network.saveEvent(defaultRace);
//
//            System.out.println("    - Cursa default creata.");

        } catch (Exception e) {
            System.err.println("Eroare critica la incarcarea datelor: " + e.getMessage());
        }
    }
}
