package main.model.repositories;

import main.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface UsersRepository extends CrudRepository<User, Integer> {

    @Query("From User as u where u.email = :email")
    User findByEmail(@Param("email") String email);

    @Query("From User as u where u.id = :id")
    User customFindById(@Param("id") int id);

    @Query("From User as u where u.isModerator = 1")
    List<User> findModerator(PageRequest pg);

    @Query("From User as u where u.code = :code")
    User findByResetCode(@Param("code") String code);
}
