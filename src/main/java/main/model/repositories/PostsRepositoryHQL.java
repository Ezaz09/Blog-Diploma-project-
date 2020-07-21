package main.model.repositories;

import main.api.responses.PostsResponse;
import main.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostsRepositoryHQL extends JpaRepository<PostsResponse, Integer> {
    @Query("select " +
            "posts.id as id, " +
            "DATE_FORMAT(posts.time, '%d-%c-%Y, %H-%i') as time_of_post, " +
            "posts.userId as user, " +
            "posts.title, " +
            "SUM( CASE " +
            "   WHEN pVotes.value = 1 THEN pVotes.value " +
            "   ELSE 0 " +
            " END) as like_count, " +
            "SUM( CASE " +
            "   WHEN pVotes.value = -1 THEN pVotes.value " +
            "   ELSE 0 " +
            " END) as dislike_count, " +
            "COUNT(pComments.id) as comment_count, " +
            "posts.viewCount as view_count " +
            "FROM Posts as posts " +
            "LEFT JOIN PostsVotes as pVotes ON pVotes.id = posts.id " +
            "LEFT JOIN PostComments as pComments ON pComments.id = posts.id " +
            "WHERE posts.isActive = 1 " +
            "AND posts.moderationStatus = 'ACCEPTED' " +
            "GROUP BY id ")
    List<PostsResponse> getAllPosts(
            @Param("mode") String mode);
}
