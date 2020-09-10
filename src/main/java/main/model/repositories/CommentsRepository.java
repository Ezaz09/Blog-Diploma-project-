package main.model.repositories;

import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsRepository extends JpaRepository<PostComment, Integer> {
   /*@Query("From PostsComments as pComments")
    List<CommentsResponse> getAllCommentsForCertainPost(
            @Param("postId") int postId);*/
}
