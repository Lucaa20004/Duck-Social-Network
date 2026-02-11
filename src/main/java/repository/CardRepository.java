package repository;

import domain.Card;
import validation.Validator;

public class CardRepository extends InMemoryRepository<Long, Card> {

    private long nextCardId = 1;

    public CardRepository(Validator<Card> validator) {
        super(validator);
    }

    @Override
    public Card save(Card entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity can't be null!");

        if (entity.getId() == null || entity.getId() == 0) {
            entity.setId(nextCardId++);
        }


        return super.save(entity);
    }
}