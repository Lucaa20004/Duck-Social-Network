package validation;

import domain.Duck;
import exeption.ValidationExeption;

/**
 * Strategie de validare specifică pentru Rață.
 */
public class DuckValidator implements Validator<Duck> {
    @Override
    public void validate(Duck entity) throws ValidationExeption {
        String errors = "";
        if (entity.getUsername() == null || entity.getUsername().trim().isEmpty()) {
            errors += "Username-ul (numele raței) nu poate fi vid! ";
        }
        if (entity.getViteza() <= 0) {
            errors += "Viteza trebuie sa fie pozitivă! ";
        }
        if (entity.getRezistenta() <= 0) {
            errors += "Rezistența trebuie sa fie pozitivă! ";
        }

        if (!errors.isEmpty()) {
            throw new ValidationExeption(errors);
        }
    }
}