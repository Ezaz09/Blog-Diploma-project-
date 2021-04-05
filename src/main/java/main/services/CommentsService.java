package main.services;

import main.model.PostComment;
import main.model.repositories.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public int addNewComment(PostComment newPostComment) {
        if (newPostComment == null) {
            return 0;
        }

        PostComment savedPostComment = commentsRepository.save(newPostComment);

        return savedPostComment.getId();
    }
}
