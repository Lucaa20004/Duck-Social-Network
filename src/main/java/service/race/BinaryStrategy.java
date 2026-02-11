package service.race;

import java.util.*;
import domain.*;

public class BinaryStrategy implements Strategy {
    @Override
    public Result solve(Duck[] ducks, Culoar[] culoare) {
        int N = ducks.length;
        int M = culoare.length;

        // Sortăm culoarele crescător după distanță (L1 -> L2 -> ...)
        Arrays.sort(culoare, Comparator.comparingDouble(Culoar::getDistanta));

        // Sortăm rațele CRESCĂTOR după rezistență (important!)
        // Dacă rezistență egală, sortăm descrescător după viteză
        Arrays.sort(ducks, (d1, d2) -> {
            int cmpRez = Double.compare(d1.getRezistenta(), d2.getRezistenta());
            if (cmpRez != 0) return cmpRez;
            return Double.compare(d1.getViteza(), d2.getViteza());
        });

        double left = 0.0, right = 1e12;
        double eps = 1e-4;

        // căutăm timpul minim posibil
        while (right - left > eps) {
            double mid = (left + right) / 2;
            if (canFinish(ducks, culoare, mid))
                right = mid;
            else
                left = mid;
        }

        // reconstrucție rezultat final pentru timpul minim
        AssignmentResult assignment = assignDucks(ducks, culoare, right);
        return new Result(right, assignment.ducks, assignment.culoare);
    }

    /**
     * Verifică dacă putem aloca M rațe pentru timp ≤ T,
     * respectând constrângerea de rezistență crescătoare.
     */
    private boolean canFinish(Duck[] ducks, Culoar[] culoare, double T) {
        boolean[] used = new boolean[ducks.length];
        int M = culoare.length;
        double lastRezistenta = -1; // rezistența ultimei rațe alocate

        for (int i = 0; i < M; i++) {
            boolean found = false;
            double dist = culoare[i].getDistanta();

            // Căutăm prima rață disponibilă care:
            // 1. Poate termina în timp T
            // 2. Are rezistență >= lastRezistenta
            for (int j = 0; j < ducks.length; j++) {
                if (used[j]) continue;

                // Verificăm constrângerea de rezistență
                if (ducks[j].getRezistenta() < lastRezistenta) continue;

                double t = 2 * dist / ducks[j].getViteza();
                if (t <= T) {
                    used[j] = true;
                    lastRezistenta = ducks[j].getRezistenta();
                    found = true;
                    break;
                }
            }

            if (!found) return false; // nu găsim pentru culoarul curent
        }
        return true; // toate culoarele au primit rață
    }

    /**
     * Clasă helper pentru a returna atât rațele cât și culoarele alocate
     */
    private static class AssignmentResult {
        Duck[] ducks;
        Culoar[] culoare;

        AssignmentResult(Duck[] ducks, Culoar[] culoare) {
            this.ducks = ducks;
            this.culoare = culoare;
        }
    }

    /**
     * Returnează efectiv lista rațelor și culoarele alese pentru timpul T,
     * respectând constrângerea de rezistență.
     */
    private AssignmentResult assignDucks(Duck[] ducks, Culoar[] culoare, double T) {
        boolean[] used = new boolean[ducks.length];
        List<Duck> rezultatDucks = new ArrayList<>();
        List<Culoar> rezultatCuloare = new ArrayList<>();
        double lastRezistenta = -1;

        for (int i = 0; i < culoare.length; i++) {
            double dist = culoare[i].getDistanta();

            for (int j = 0; j < ducks.length; j++) {
                if (used[j]) continue;

                // Verificăm constrângerea de rezistență
                if (ducks[j].getRezistenta() < lastRezistenta) continue;

                double t = 2 * dist / ducks[j].getViteza();
                if (t <= T) {
                    used[j] = true;
                    lastRezistenta = ducks[j].getRezistenta();
                    rezultatDucks.add(ducks[j]);
                    rezultatCuloare.add(culoare[i]);
                    break;
                }
            }
        }

        return new AssignmentResult(
                rezultatDucks.toArray(new Duck[0]),
                rezultatCuloare.toArray(new Culoar[0])
        );
    }
}
