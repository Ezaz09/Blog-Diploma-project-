package main.model.repositories;

import main.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends JpaRepository<PostVote, Integer> {
    @Query("From PostVote as pV where pV.userId = :userId and pV.postId = :postId")
    PostVote findPostVoteByUserId(@Param("userId") int userId, @Param("postId") int postId);
}
