package main.model.repositories;

import main.model.Tag;
import main.model.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag2PostRepository extends JpaRepository<Tag2Post, Integer> {
    @Query("From Tag2Post as t2p " +
            " WHERE t2p.postId = :postId" +
            " AND t2p.tag = :tag")
    Tag2Post getTag2PostByIdAndTag(@Param("postId") int id, @Param("tag") Tag tag);
}
