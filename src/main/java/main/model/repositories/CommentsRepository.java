package main.model.repositories;

import main.model.PostsComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<PostsComments, Integer> {
   /*@Query("From PostsComments as pComments")
    List<CommentsResponse> getAllCommentsForCertainPost(
            @Param("postId") int postId);*/
}
