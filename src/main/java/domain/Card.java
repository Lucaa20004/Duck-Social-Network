package domain;

import java.util.HashMap;
import java.util.Map;

public abstract class Card implements Entity<Long>{
    private Long id;
    private String numeCard;
    private final Map<Long, Duck> membri;

    public Card( String numeCard, Long id) {
        this.membri = new HashMap<>();
        this.numeCard = numeCard;
        this.id = id;
    }
    public abstract boolean canjoin(Duck d);

    public void addMembru(Duck d) throws  Exception{
        if(canjoin(d)) {
            this.membri.put(d.getId(), d);
        }
        else {
            throw new Exception("Rața " + d.getUsername() + " nu este compatibilă cu cârdul " + numeCard);
        }
    }

    public void removeMembru(Duck d) {
        membri.remove(d.getId());
    }
    public String getPrtfomance() {
        if(membri.isEmpty()) {
            return "Cardul e gol";
        }
        double vitezaTotala = 0;
        double rezistentaTotala = 0;
        for(Duck d : membri.values()) {
            vitezaTotala += d.getViteza();
            rezistentaTotala += d.getRezistenta();
        }
        return String.format("Performanță medie: Viteză=%.2f, Rezistență=%.2f",
                vitezaTotala / membri.size(),
                rezistentaTotala / membri.size());
    }

//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    public String getNumeCard() { return numeCard; }
    public Map<Long, Duck>getMembri() {
        return membri;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", numeCard='" + numeCard + '\'' +
                '}';
    }
}
