package ui;

import domain.*;
import exeption.UserNotFoundExeption;
import exeption.ValidationExeption;
import repository.NetworkManager;
import service.DuckService;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleUI {
    private final DuckService networkManager;
    private final Scanner scanner;


    public ConsoleUI(DuckService networkManager) {
        this.networkManager = networkManager;
        this.scanner = new Scanner(System.in);
    }


    public void run() {
        while (true) {
            showMenu();
            String optiune = scanner.nextLine();
            switch (optiune) {
                case "1":
                    uiAddUser();
                    break;
                case "2":
                    uiRemoveUser();
                    break;
                case "3":
                    uiAddFriendship();
                    break;
                case "4":
                    uiRemoveFriendship();
                    break;
                case "5":
                    uiShowCommunityCount();
                    break;
                case "6":
                    uiShowMostSociableCommunity();
                    break;
                case "7":
                    uiShowNetwork();
                    break;
                case "8":
                    uiShowCarduri();
                    break;
                case "9":
                    uiAddCard();
                    break;
                case "10":
                    uiAddDuckToCard();
                    break;
                case "11":
                    uiaddEvetn();
                    break;
                case "12":
                    uiRunEvent();
                    break;
                case "13":
                    uiSubscribeEvent();
                    break;
                case "14":
                    uiUnscubscribeEvent();
                    break;
                case "15":
                    uiShowSubscribers();
                    break;
                case "16":
                    uiShowMesaje();
                    break;
                case "0":
                    System.out.println("La revedere! Quack!");
                    return;
                default:
                    System.out.println("Optiune invalida! Vă rugam reincercati.");
            }
            System.out.println("\n(Apasati Enter pentru a continua...)");
            scanner.nextLine();
        }
    }

    private void showMenu() {
        System.out.println("--- Meniu DuckSocialNetwork ---");
        System.out.println("1. Adaugare utilizator (Persoana / Rata)");
        System.out.println("2. Stergere utilizator");
        System.out.println("3. Adaugare prietenie");
        System.out.println("4. Stergere prietenie");
        System.out.println("==============================");
        System.out.println("5. Afisare numar comunitati");
        System.out.println("6. Afisare cea mai sociabila comunitate");
        System.out.println("7. Afisare completa retea (utilizatori si prietenii)");
        System.out.println("==============================");
        System.out.println("8. Afisare carduri si membrii lor");
        System.out.println("9. Adaugare carduri");
        System.out.println("10. Adaugare ducks la carduri");
        System.out.println("==============================");
        System.out.println("11. Adauga Event");
        System.out.println("12. Run Eveniment");
        System.out.println("13. Subscribe");
        System.out.println("14. Unsubscribe");
        System.out.println("15. Afiseaza lista subscribers");
        System.out.println("==============================");
        System.out.println("16. Afiseaza mesaje user");
        System.out.println("0. Iesire");
        System.out.print("Alegeti optiunea: ");
    }

    private void uiAddCard() {
        System.out.print("tip de rate din card(swim/fly): ");
        String tip = scanner.nextLine();
        if(tip.equals("swim")) {
            System.out.print("Nume: ");
            String nume = scanner.nextLine();
            Card c = new SwimCard(nume, null);
            networkManager.addCard(c);
        }
        else{
            System.out.print("Nume: ");
            String nume = scanner.nextLine();
            Card c = new FlyCard(nume, null);
            networkManager.addCard(c);
        }
    }
    private void uiSubscribeEvent() {
        try{
            System.out.print("Introduceti ID ul userului care se va abona la eveniment: ");
            Long userID = Long.parseLong(scanner.nextLine());
            System.out.print("Introduceti ID ul evenimentului: ");
            Long eventID = Long.parseLong(scanner.nextLine());

            networkManager.subscribeToEvent(userID, eventID);
        }
        catch (NumberFormatException e) {
            System.err.println("ID invalid! Trebuie să fie un numar.");
        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
        }

    }

    private void uiShowSubscribers() {
        try{
            System.out.print("Introduceti ID ul evenimentului: ");
            Long eventID = Long.parseLong(scanner.nextLine());

            networkManager.afiseazaSubscriber(eventID);
        }
        catch (NumberFormatException e) {
            System.err.println("ID invalid! Trebuie să fie un numar.");
        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
        }
    }

    private void uiUnscubscribeEvent() {
        try{
            System.out.print("Introduceti ID ul userului care se va dezabona de la eveniment: ");
            Long userID = Long.parseLong(scanner.nextLine());
            System.out.print("Introduceti ID ul evenimentului: ");
            Long eventID = Long.parseLong(scanner.nextLine());

            networkManager.unsubscribeRoEvent(userID, eventID);
        }
        catch (NumberFormatException e) {
            System.err.println("ID invalid! Trebuie să fie un numar.");
        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
        }
    }

    private void uiRunEvent() {
        try {
            System.out.print("Introduceti ID-ul cursei care sa porneasca: ");
            Long eventId = Long.parseLong(scanner.nextLine());

            networkManager.runRaceEvent(eventId);

        } catch (NumberFormatException e) {
            System.err.println("ID invalid! Trebuie să fie un număr.");
        } catch (Exception e) {
            System.err.println("Eroarea la rularea cursei: " + e.getMessage());
        }
    }

    private void uiaddEvetn() {

        try {
            System.out.print("Numele cursei: ");
            String nume = scanner.nextLine();
            System.out.print("Introduceti distantele culoarelor (separate prin spatiu): ");
            String distanteStr = scanner.nextLine();

            List<Culoar> culoareList = Arrays.stream(distanteStr.split(" "))
                    .map(Double::parseDouble)
                    .map(Culoar::new)
                    .collect(Collectors.toList());

            RaceEvent event = new RaceEvent(null, nume, culoareList);
            networkManager.saveEvent(event);

        } catch (NumberFormatException e) {
            System.err.println("Eroare: Distanțele trebuie să fie numere valide.");
        } catch (Exception e) {
            System.err.println("Eroare la crearea cursei: " + e.getMessage());
        }

    }


    private void uiAddUser() {
        System.out.print("Tip utilizator (person / duck): ");
        String tip = scanner.nextLine().trim().toLowerCase();

        try {
            if ("person".equals(tip)) {
                addPerson();
            } else if ("duck".equals(tip)) {
                addDuck();
            } else {
                System.out.println("Tip de utilizator necunoscut. Alegeti 'person' sau 'duck'.");
            }
        } catch (ValidationExeption e) {
            System.err.println("Eroare de validare: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Eroare de format: valoarea numerica introdusa este invalida.");
        } catch (IllegalArgumentException e) {
            System.err.println("Eroare: Tip de rata invalid. Folositi: FLYING, SWIMMING, FLYING_AND_SWIMMING.");
        } catch (Exception e) {
            System.err.println("O eroare neasteptata a avut loc: " + e.getMessage());
        }
    }

    private void addPerson() throws ValidationExeption {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Parola: ");
        String pass = scanner.nextLine();
        System.out.print("Nume: ");
        String nume = scanner.nextLine();
        System.out.print("Prenume: ");
        String prenume = scanner.nextLine();
        System.out.print("Ocupatie: ");
        String ocupatie = scanner.nextLine();
        System.out.print("Nivel Empatie (0-10): ");
        int empatie = Integer.parseInt(scanner.nextLine());

        Person p = new Person(null, username, email, pass, nume, prenume, LocalDate.now(), ocupatie, empatie);
        networkManager.addUser(p);
        System.out.println("Persoana adaugata cu succes! ID nou: " + p.getId());
    }

    private void uiShowMesaje() {
        System.out.print("ID user: ");
        Long userID = Long.parseLong(scanner.nextLine());

        networkManager.afiseazaMesaje(userID);
    }

    private void addDuck() throws ValidationExeption, IllegalArgumentException {
        System.out.print("Username (Numele ratei): ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Parola (de ex. 'quack'): ");
        String pass = scanner.nextLine();
        System.out.print("Viteza: ");
        double viteza = Double.parseDouble(scanner.nextLine());
        System.out.print("Rezistenta: ");
        double rezistenta = Double.parseDouble(scanner.nextLine());
        System.out.print("tip rata(flying sau swimming): ");
        String tipRata = scanner.nextLine();
        if ("flying".equals(tipRata)) {
            Duck d = new FlyingDuck(null, username, email, pass, viteza, rezistenta);
            networkManager.addUser(d);
            System.out.println("Rata adaugata cu succes! ID nou: " + d.getId());
        }
        else {
            Duck d = new SwimmingDuck(null, username, email, pass, viteza, rezistenta);
            networkManager.addUser(d);
            System.out.println("Rata adaugata cu succes! ID nou: " + d.getId());
        }
    }

    private void uiAddDuckToCard() {
        try {
            System.out.print("Introduceti ID-ul Cardului: ");
            String cardIdStr = scanner.nextLine();
            Long idCard = Long.parseLong(cardIdStr);

            System.out.print("Introduceti ID-ul utilizatorului (Raței): ");
            String duckIdStr = scanner.nextLine();
            Long idDuck = Long.parseLong(duckIdStr);

            // Apelăm serviciul
            networkManager.addToCard(idCard, idDuck);

        } catch (NumberFormatException e) {
            // Aici intră DOAR dacă ai scris litere
            System.err.println("ID invalid! Trebuie sa fie un numar (ex: 1, 5, 100).");
        } catch (Exception e) {
            // Aici intră erorile de logică (UserNotFound, CardNotFound etc.)
            // AFISĂM MESAJUL REAL AL ERORII!
            System.err.println("Eroare la adăugare: " + e.getMessage());
        }
    }

    private void uiRemoveUser() {
        try {
            System.out.print("Introduceti ID-ul utilizatorului de sters: ");
            Long id = Long.parseLong(scanner.nextLine());
            networkManager.removeUser(id);
            System.out.println("Utilizator sters cu succes.");
        } catch (NumberFormatException e) {
            System.err.println("ID invalid! Trebuie să fie un numar.");
        } catch (UserNotFoundExeption e) {
            System.err.println(e.getMessage());
        }
    }

    private void uiAddFriendship() {
        try {
            System.out.print("Introduceti ID-ul primului utilizator: ");
            Long id1 = Long.parseLong(scanner.nextLine());
            System.out.print("Introduceti ID-ul celui de-al doilea utilizator: ");
            Long id2 = Long.parseLong(scanner.nextLine());
            networkManager.addFriendship(id1, id2);
        } catch (NumberFormatException e) {
            System.err.println("ID invalid! Trebuie să fie un număr.");
        } catch (UserNotFoundExeption e) {
            System.err.println(e.getMessage());
        }
    }

    private void uiRemoveFriendship() {
        try {
            System.out.print("Introduceti ID-ul primului utilizator: ");
            Long id1 = Long.parseLong(scanner.nextLine());
            System.out.print("Introduceti ID-ul celui de-al doilea utilizator: ");
            Long id2 = Long.parseLong(scanner.nextLine());
            networkManager.removeFriendship(id1, id2);
            System.out.println("Prietenie stearsa.");
        } catch (NumberFormatException e) {
            System.err.println("ID invalid! Trebuie sa fie un numar.");
        } catch (UserNotFoundExeption e) {
            System.err.println(e.getMessage());
        }
    }

    private void uiShowCommunityCount() {
        int count = networkManager.getCommunityCount();
        System.out.println("Numarul total de comunitati (grupuri de prieteni) este: " + count);
    }

    private void uiShowMostSociableCommunity() {
        System.out.println("Se calculeaza cea mai sociabila comunitate (diametru maxim)...");
        Set<User> community = networkManager.getMostSociableCommunity();
        if (community == null || community.isEmpty()) {
            System.out.println("Reteaua este goala.");
            return;
        }

        System.out.println("Cea mai sociabila comunitate este:");
        community.stream()
                .map(user -> "- " + user)
                .forEach(System.out::println);
    }

    private void uiShowCarduri() {
        networkManager.afiseazaCard();

    }

    private void uiShowNetwork() {
        networkManager.afiseazaRetea();
    }
}