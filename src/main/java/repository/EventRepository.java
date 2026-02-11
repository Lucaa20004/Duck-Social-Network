    package repository;

    import domain.Event;
    import validation.Validator;

    public class EventRepository extends InMemoryRepository<Long, Event> {

        private long nextEventId = 1;

        public EventRepository(Validator<Event> validator) {
            super(validator);
        }

        @Override
        public Event save(Event entity) {
            if (entity == null)
                throw new IllegalArgumentException("Entity can't be null!");

            if (entity.getId() == null || entity.getId() == 0) {
                entity.setId(nextEventId++);
            }
            return super.save(entity);
        }
    }