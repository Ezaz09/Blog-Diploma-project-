package main.model.repositories;

import main.api.responses.PostsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<PostsResponse, Integer> {
    @Query(value = "select " +
            "posts.id, " +
            "posts.time, " +
            "posts.title, " +
            "posts.user_id, " +
            "SUM( CASE " +
            "   WHEN pVotes.value = 1 THEN pVotes.value " +
            "   ELSE 0 " +
            " END) as like_count, " +
            "SUM( CASE " +
            "   WHEN pVotes.value = -1 THEN pVotes.value " +
            "   ELSE 0 " +
            " END) as dislike_count, " +
            "posts.view_count as view_count," +
            "COUNT(pComments.id) as comment_count " +
            "FROM posts as posts " +
            "JOIN posts_votes as pVotes ON pVotes.post_id = posts.id " +
            "JOIN post_comments as pComments ON pComments.post_id = posts.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<PostsResponse> getAllPosts(
            @Param("offset") int offset,
            @Param("limit") int limit);
}
