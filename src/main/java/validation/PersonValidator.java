package validation;

import domain.Person;
import exeption.ValidationExeption;

/**
 * Strategie de validare specifică pentru Persoană.
 */
public class PersonValidator implements Validator<Person> {
    @Override
    public void validate(Person entity) throws ValidationExeption {
        String errors = "";
        if (entity.getNume() == null || entity.getNume().trim().isEmpty()) {
            errors += "Numele nu poate fi vid! ";
        }
        if (entity.getPrenume() == null || entity.getPrenume().trim().isEmpty()) {
            errors += "Prenumele nu poate fi vid! ";
        }
        if (entity.getUsername() == null || entity.getUsername().trim().isEmpty()) {
            errors += "Username-ul nu poate fi vid! ";
        }
        if (entity.getNivelEmpatie() < 0 || entity.getNivelEmpatie() > 10) {
            errors += "Nivelul de empatie trebuie sa fie intre 0 si 10! ";
        }

        if (!errors.isEmpty()) {
            throw new ValidationExeption(errors);
        }
    }
}