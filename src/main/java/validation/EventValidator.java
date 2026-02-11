package validation;

import domain.Event;
import exeption.ValidationExeption;

public class EventValidator implements Validator<Event> {
    @Override
    public void validate(Event entity) throws ValidationExeption {
        String errors = "";
        if (entity.getNume() == null || entity.getNume().trim().isEmpty()) {
            errors += "Numele evenimentului nu poate fi vid! ";
        }

        if (!errors.isEmpty()) {
            throw new ValidationExeption(errors);
        }
    }
}