package main.model.repositories;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagsRepository extends JpaRepository<Tag, Integer> {
    @Query("From Tag as t where t.name = :name")
    Tag findByTagName(@Param("name") String name);
}
