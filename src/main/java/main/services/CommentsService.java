package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.CommentResponse;
import main.model.repositories.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository)
    {
        this.commentsRepository = commentsRepository;
    }

    public List<CommentResponse> getAllCommentsForCertainPost(int postId)
    {
        return null;
    }
}
