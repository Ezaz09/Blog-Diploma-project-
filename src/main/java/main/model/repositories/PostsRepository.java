package main.model.repositories;

import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;

@Repository
public interface PostsRepository extends JpaRepository<Post, Integer> {
    @Query("From Post as p order by size(p.likeVotes) desc")
    List<Post> getPostsSortByLikeVotes(PageRequest pR);

    @Query("From Post as p order by size(p.comments) desc")
    List<Post> getPostsSortByComments(PageRequest pR);

    @Query("From Post as p where p.id = :postId")
    Post getCertainPost(@Param("postId") int postId);

    @Query("From Post as p where p.title LIKE :query")
    List<Post> getPostsByQuery(Pageable pageable, @Param("query") String query);

    @Query("From Post as p where p.time between :startOfDay and :endOfDay")
    List<Post> getPostsByDate(Pageable pageable, @Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);

    @Query("From Post as p " +
            " LEFT JOIN Tag2Post as t2p ON p.id = t2p.postId" +
            " LEFT JOIN Tag as t ON t2p.tag = t.id" +
            " WHERE t.name = :tag")
    List<Post> getPostsByTag(Pageable pageable, @Param("tag") String tag);

    @Query("From Post as p where p.user = :user")
    List<Post> getUserPosts(@Param("user") User user);

    @Query("From Post as p where p.user = :userId " +
            "and p.isActive = 0")
    List<Post> getInactiveUserPosts(Pageable pageable, @Param("userId") User user);

    @Query("From Post as p where p.user = :userId " +
            "and p.isActive = 1 " +
            "and p.moderationStatus = 'NEW'")
    List<Post> getPendingUserPosts(Pageable pageable, @Param("userId") User user);

    @Query("From Post as p where p.user = :userId " +
            "and p.isActive = 1 " +
            "and p.moderationStatus = 'DECLINED'")
    List<Post> getDeclinedUserPosts(Pageable pageable, @Param("userId") User user);

    @Query("From Post as p where p.user = :userId " +
            "and p.isActive = 1 " +
            "and p.moderationStatus = 'ACCEPTED'")
    List<Post> getPublishedUserPosts(Pageable pageable, @Param("userId") User user);

    @Query("From Post as p where p.moderator = :userId" +
            " and p.moderationStatus = :status")
    List<Post> getModeratorPosts(Pageable pageable, @Param("userId") User moderator, @Param("status") ModerationStatus moderationStatus);

    @Query("From Post as p where year(p.time) = :year order by p.time desc ")
    List<Post> getCountOfPostsPerYear(@Param("year") Integer year);
}
