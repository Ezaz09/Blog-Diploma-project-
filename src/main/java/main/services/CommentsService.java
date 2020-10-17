package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.CommentRequest;
import main.api.responses.NewCommentResponse;
import main.model.PostComment;
import main.model.User;
import main.model.repositories.CommentsRepository;
import main.model.repositories.UserRepository;
import main.services.mappers.CommentMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;

@Slf4j
@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository,
                           UserRepository userRepository) {
        this.commentsRepository = commentsRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<NewCommentResponse> addNewComment(CommentRequest commentRequest,
                                                            Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        PostComment newPostComment = new CommentMapperImpl().commentRequestToPostComment(commentRequest, user);

        if (newPostComment == null) {
            NewCommentResponse newCommentResponse = new NewCommentResponse();
            newCommentResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>(1);
            errors.put("text", "Произошла непредвиденная ошибка при добавлении нового коментария! Сообщите об этом администратору!");

            newCommentResponse.setErrors(errors);

            return new ResponseEntity<>(newCommentResponse, HttpStatus.OK);
        }

        PostComment savedPostComment = commentsRepository.save(newPostComment);

        NewCommentResponse newCommentResponse = new NewCommentResponse();
        newCommentResponse.setId(savedPostComment.getId());

        return new ResponseEntity<>(newCommentResponse, HttpStatus.OK);
    }
}
