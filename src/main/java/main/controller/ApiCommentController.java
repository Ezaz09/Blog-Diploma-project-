package main.controller;

import main.api.mappers.CommentMapper;
import main.api.requests.CommentRequest;
import main.api.responses.NewCommentResponse;
import main.model.PostComment;
import main.model.User;
import main.model.repositories.UsersRepository;
import main.services.CommentsService;
import main.services.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;

@RestController
@RequestMapping("/api/comment")
public class ApiCommentController{
    private final CommentsService commentService;
    private final CommentMapper commentMapper;
    private final ResponseService responseService;
    private final UsersRepository usersRepository;

    @Autowired
    public ApiCommentController(CommentsService commentService,
                                CommentMapper commentMapper,
                                ResponseService responseService,
                                UsersRepository usersRepository) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.responseService = responseService;
        this.usersRepository = usersRepository;
    }

    @PostMapping(path = "")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<NewCommentResponse> addNewComment(@RequestBody CommentRequest commentRequest,
                                                            Principal principal) {
        NewCommentResponse newCommentResponse = responseService.createNewCommentResponse();
        User user = this.findUserByEmail(principal.getName());

        PostComment newPostComment = commentMapper.commentRequestToPostComment(commentRequest, user);

        if(newPostComment == null) {
            newCommentResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>(1);
            errors.put("text", "Произошла непредвиденная ошибка при добавлении нового комментария! Сообщите об этом администратору!");

            newCommentResponse.setErrors(errors);

            return new ResponseEntity<>(newCommentResponse, HttpStatus.OK);
        }

        int idOfSavedNewPostComment = commentService.addNewComment(newPostComment);
        newCommentResponse.setId(idOfSavedNewPostComment);

        if(idOfSavedNewPostComment == 0) {
            newCommentResponse.setResult(false);
            HashMap<String, String> errors = new HashMap<>();
            errors.put("unsavedPost", "Произошла ошибка при сохранении комментария!");

            newCommentResponse.setErrors(errors);
        }

        return new ResponseEntity<>(newCommentResponse,HttpStatus.OK);
    }


    private User findUserByEmail(String email) {
        return this.usersRepository.findByEmail(email);
    }
}
