package domain;

import domain.tiprata.Inotator; // Interfața Inotator
import service.race.*; // Importă pachetul cu logica Natație
import domain.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class RaceEvent extends Event{
    private final List<Culoar> culoare;
    private final List<Duck> participants;

    public RaceEvent(Long id, String nume, List<Culoar> culoare) {
        super(id, nume);
        this.culoare = culoare;
        this.participants = new  ArrayList<>();
    }

    public void addParticipant(Duck duck) throws Exception {
        if (duck instanceof Inotator) {
            if (!participants.contains(duck)) {
                participants.add(duck);
            }
        } else {
            throw new Exception(duck.getUsername() + " nu poate participa la o cursă de înot!");
        }
    }

    private void findAllParticipants(Map<Long, User> allUsers) {
        this.participants.clear();

        for (User user : allUsers.values()) {
            if (user instanceof Inotator) {
                this.participants.add((Duck) user);
            }
        }
    }

    public String runRace(Map<Long, User> allUsers) {

        findAllParticipants(allUsers);
        System.out.println("Am gasit " + participants.size() + " inotatori eligibili in retea.");

        if(participants.isEmpty()) {
            return "Cursa anulata, nu sunt rante in baza de date";
        }

        Culoar[] culoareArray = this.culoare.toArray(new Culoar[0]);

        Duck[] raceDucks = new Duck[participants.size()];
        for (int i = 0; i < participants.size(); i++) {
            Duck d = participants.get(i);
            raceDucks[i] = d;
        }

        if (raceDucks.length < culoareArray.length) {
           return "Nu sunt destui participanti";
        }

        StrategyFactory factory = new DuckStrategyFactory();
        Strategy strategy = factory.createStrategy(Strategy_type.BINARY_S);
        Result result = strategy.solve(raceDucks, culoareArray);

        Duck[] ducks = result.getDucks();
        Culoar[] culoare = result.getCuloare();
        StringBuilder sb = new StringBuilder();
        sb.append("Rezultat cursa").append(getNume()).append("\n");

        for (int i = 0; i < ducks.length; i++) {
            double timpCuloar = 2 * culoare[i].getDistanta() / ducks[i].getViteza();

            sb.append(String.format("%d. %s (Culoar %d) - %.2f sec\n",
                    (i + 1),
                    ducks[i].getUsername(),
                    (i+ 1),
                    timpCuloar
            ));

        }

        //notifySubscribers(rezultatCursa);
        return sb.toString();
    }

    public List<Culoar> getCuloare() {
        return culoare;
    }

    public List<Duck> getParticipants() {
        return participants;
    }
}
