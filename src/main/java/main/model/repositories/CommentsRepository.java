package main.model.repositories;

import main.api.responses.CommentsResponse;
import main.model.PostsComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<PostsComments, Integer> {
   /*@Query("From PostsComments as pComments")
    List<CommentsResponse> getAllCommentsForCertainPost(
            @Param("postId") int postId);*/
}
