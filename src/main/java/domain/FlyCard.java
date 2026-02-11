package domain;

import domain.tiprata.Zburator;

public class FlyCard extends Card{
    public FlyCard(String numeCard, Long id) {
        super(numeCard, id);
    }
    @Override
    public boolean canjoin(Duck d) {
        return (d instanceof Zburator);
    }
}
