package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.CommentRequest;
import main.api.responses.NewCommentResponse;
import main.model.PostComment;
import main.model.User;
import main.model.repositories.CommentsRepository;
import main.model.repositories.UsersRepository;
import main.api.mappers.CommentMapper;
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
    private final UsersRepository usersRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository,
                           UsersRepository usersRepository) {
        this.commentsRepository = commentsRepository;
        this.usersRepository = usersRepository;
    }

    public ResponseEntity<NewCommentResponse> addNewComment(CommentRequest commentRequest,
                                                            Principal principal) {
        User user = usersRepository.findByEmail(principal.getName());
        PostComment newPostComment = new CommentMapper().commentRequestToPostComment(commentRequest, user);

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
