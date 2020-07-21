package main.model.repositories;

import main.api.responses.CertainPostResponse;
import main.api.responses.PostsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<PostsResponse, Integer> {
    @Query(value = "select " +
            "posts.id as id, " +
            "DATE_FORMAT(posts.time, '%d-%c-%Y, %H-%i') as time_of_post, " +
            "posts.title, " +
            "posts.user_id as user_id, " +
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
            "LEFT JOIN posts_votes as pVotes ON pVotes.post_id = posts.id " +
            "LEFT JOIN post_comments as pComments ON pComments.post_id = posts.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "GROUP BY id " +
            "ORDER BY " +
            "CASE WHEN :mode = 'recent' THEN time_of_post END ASC, " +
            "CASE WHEN :mode = 'popular' THEN comment_count END DESC, " +
            "CASE WHEN :mode = 'best' THEN like_count END DESC, " +
            "CASE WHEN :mode = 'early' THEN time_of_post END DESC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<PostsResponse> getAllPosts(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("mode") String mode);

    @Query(value = "select " +
            "posts.id, " +
            "DATE_FORMAT(posts.time, '%d-%c-%Y, %H-%i') as time_of_post, " +
            "posts.title, " +
            "posts.user_id as user_id, " +
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
            "LEFT JOIN posts_votes as pVotes ON pVotes.post_id = posts.id " +
            "LEFT JOIN post_comments as pComments ON pComments.post_id = posts.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND posts.title LIKE :query " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<PostsResponse> getSomePosts(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("query") String query);

    @Query(value = "select " +
            "posts.id, " +
            "DATE_FORMAT(posts.time, '%d-%c-%Y, %H-%i') as time_of_post, " +
            "posts.title, " +
            "posts.user_id as user_id, " +
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
            "LEFT JOIN posts_votes as pVotes ON pVotes.post_id = posts.id " +
            "LEFT JOIN post_comments as pComments ON pComments.post_id = posts.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND posts.id = :postId " +
            "AND posts.time <= NOW()", nativeQuery = true)
    PostsResponse getCertainPost(
            @Param("postId") int postId);

    @Query(value = "select " +
            "posts.id as id, " +
            "DATE_FORMAT(posts.time, '%d-%c-%Y, %H-%i') as time_of_post, " +
            "posts.title as title, " +
            "posts.user_id as user_id, " +
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
            "LEFT JOIN posts_votes as pVotes ON pVotes.post_id = posts.id " +
            "LEFT JOIN post_comments as pComments ON pComments.post_id = posts.id " +
            "WHERE posts.is_active = 1 " +
            "AND posts.moderation_status = 'ACCEPTED' " +
            "AND date_format(posts.time, '%Y-%c-%d') = date_format(:dateForSelection,'%Y-%c-%d') " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<PostsResponse> getSomePostsByDate(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("dateForSelection") String dateForSelection);
}
