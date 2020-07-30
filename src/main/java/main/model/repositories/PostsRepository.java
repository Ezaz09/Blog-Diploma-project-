package main.model.repositories;

import main.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {
    @Query("From Posts as p where p.id = :postId")
    Posts getCertainPost(@Param("postId") int postId);

    @Query("From Posts as p where p.title LIKE :query")
    List<Posts> getSomePosts(@Param("query") String query);

    @Query("From Posts as p where FORMAT(p.time, 'yyyy-MM-dd') = :time")
    List<Posts> getSomePostsByDate(@Param("time") Date time);

    @Query("From Posts as p " +
            " LEFT JOIN Tag2Post as t2p ON p.id = t2p.postId" +
            " LEFT JOIN Tags as t ON t2p.tagId = t.id" +
            " WHERE t.name = :tag")
    List<Posts> getSomePostsByTag(@Param("tag") String tag);
}
