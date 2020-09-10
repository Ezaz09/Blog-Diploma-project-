package main.model.repositories;

import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("From User as u where u.email = :email")
    User findByEmail(@Param("email") String email);

    @Query("From User as u where u.id = :id")
    User customFindById(@Param("id") int id);
}
