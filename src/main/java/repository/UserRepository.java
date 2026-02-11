package repository;

import domain.Duck;
import domain.Entity;
import domain.Person;
import domain.User;
import exeption.ValidationExeption;
import validation.DuckValidator;
import validation.PersonValidator;
import validation.Validator;

public class UserRepository extends InMemoryRepository<Long, User> {

    private final Validator<Person> personValidator;
    private final Validator<Duck> duckValidator;
    private long nextId = 1;

    public UserRepository(Validator<User> validator) {
        super(validator);
        this.personValidator = new PersonValidator();
        this.duckValidator = new DuckValidator();
    }


    @Override
    public User save(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity can't be null!");

        if (entity instanceof Person) {
            personValidator.validate((Person) entity);
        } else if (entity instanceof Duck) {
            duckValidator.validate((Duck) entity);
        }

        if (entity.getId() == null || entity.getId() == 0) {
            entity.setId(nextId++);
        }

        if (findOne(entity.getId()) != null)
            return null;

        System.out.println("Utilizator adaugat: " + entity);
        return entities.put(entity.getId(), entity);
    }
}