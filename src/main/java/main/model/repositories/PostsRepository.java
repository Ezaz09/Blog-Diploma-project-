package main.model.repositories;

import main.api.responses.PostsResponse;
import main.model.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
//WHERE is_active = 1 AND moderation_status = 'ACCEPTED'
@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {
    @Query(value = "SELECT\n" +
            "    p.id AS id,\n" +
            "    p.time AS time,\n" +
            "    p.user_id AS user,\n" +
            "    p.title AS title,\n" +
            "    p.title AS announce,\n" +
            "    sum(CASE\n" +
            "        WHEN p_votes.value = 1 THEN p_votes.value\n" +
            "    END) AS likeCount,\n" +
            "    sum(CASE\n" +
            "        WHEN p_votes.value = - 1 THEN p_votes.value\n" +
            "    END) AS dislikeCount,\n" +
            "    0 AS commentCount,\n" +
            "    p.view_count AS viewCount\n" +
            "FROM\n" +
            "    posts AS p\n" +
            "        LEFT JOIN\n" +
            "    posts_votes AS p_votes ON p.id = p_votes.post_id\n" +
            "WHERE\n" +
            "    is_active = 1\n" +
            "        AND moderation_status = 'ACCEPTED'", nativeQuery = true)
    List<PostsResponse> getAllPosts(
            @Param("offset") int offset,
            @Param("limit") int limit);
}
