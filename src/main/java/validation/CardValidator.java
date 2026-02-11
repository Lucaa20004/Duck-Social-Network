package validation;

import domain.Card;
import exeption.ValidationExeption;

public class CardValidator implements Validator<Card> {
    @Override
    public void validate(Card entity) throws ValidationExeption {
        if (entity.getNumeCard() == null || entity.getNumeCard().trim().isEmpty()) {
            throw new ValidationExeption("Numele cârdului nu poate fi vid!");
        }
    }
}