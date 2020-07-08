package main.model.repositories;

import main.api.responses.CommentsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsResponse, Integer> {
    @Query(value = "select " +
            "comments.id, " +
            "DATE_FORMAT(comments.time, '%d-%c-%Y, %H-%i') as time, " +
            "comments.text, " +
            "comments.user_id as user_id " +
            "FROM post_comments as comments " +
            "WHERE comments.post_id = :postId", nativeQuery = true)
    List<CommentsResponse> getAllCommentsForCertainPost(
            @Param("postId") int postId);
}
