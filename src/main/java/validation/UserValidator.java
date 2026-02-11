package validation;

import domain.User;
import exeption.ValidationExeption;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationExeption {
        String errors = "";

        if (entity.getUsername() == null || entity.getUsername().trim().isEmpty()) {
            errors += "Username-ul nu poate fi vid! ";
        }
        if (entity.getEmail() == null || entity.getEmail().trim().isEmpty()) {
            errors += "Email-ul nu poate fi vid! ";
        }
        // Parolele ar trebui verificate și ele (ex: lungime minimă)
        if (entity.getPassword() == null || entity.getPassword().trim().isEmpty()) {
            errors += "Parola nu poate fi vidă! ";
        }

        if (!errors.isEmpty()) {
            throw new ValidationExeption(errors);
        }
    }
}