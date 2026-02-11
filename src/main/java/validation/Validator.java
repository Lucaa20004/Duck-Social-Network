package validation;

import exeption.ValidationExeption;
public interface Validator <T>{
    void validate(T entity) throws ValidationExeption;
}
