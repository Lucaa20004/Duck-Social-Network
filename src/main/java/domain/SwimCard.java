package domain;

import domain.tiprata.Inotator;

public class SwimCard extends Card{
    public SwimCard(String numeCard, Long id) {
        super(numeCard, id);
    }

    @Override
    public boolean canjoin(Duck d) {
        return (d instanceof Inotator);
    }
}
