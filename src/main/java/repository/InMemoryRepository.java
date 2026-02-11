package repository;



import domain.Entity;
import validation.Validator;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    Map<ID, E> entities;
    Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }


    @Override
    public E findOne(ID id) {
        if(id == null)
            throw new IllegalArgumentException("Id can't be null!");
        return entities.get(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public E save(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity can't be null!");
        validator.validate(entity);

        if(findOne(entity.getId()) != null)
            return null;

        return entities.put(entity.getId(), entity);
    }

    @Override
    public E delete(ID id) {
        if(id == null) {
            throw new IllegalArgumentException("Id can't be null!");
        }
        return entities.remove(id);
    }

    @Override
    public E update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity can't be null!");
        if(findOne(entity.getId()) == null)
            return entity;

        validator.validate(entity);
        entities.put(entity.getId(), entity);
        return null;
    }

    public Map<ID, E> getEntities() {
        return entities;
    }
}
