package repository;
import domain.Entity;
import paging.*;
public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAllOnPage(Pageable pageable);
}
