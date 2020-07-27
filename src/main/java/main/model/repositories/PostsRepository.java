package main.model.repositories;

import main.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {
    @Query("From Posts as p where p.id = :postId")
    Posts getCertainPost(@Param("postId") int postId);

    @Query("From Posts as p where p.title LIKE :query")
    List<Posts> getSomePosts(@Param("query") String query);
}
