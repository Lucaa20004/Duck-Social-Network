package repository;
import domain.*;
import paging.Page;
import paging.Pageable;

import java.util.List;

public interface DuckRepository extends PagingRepository<Long, User>{
    Page<User> findAllOnPage(Pageable pageable, String filterType);
}
