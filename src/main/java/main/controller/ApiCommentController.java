package main.controller;

import main.api.requests.CommentRequest;
import main.api.responses.NewCommentResponse;
import main.services.CommentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/comment")
public class ApiCommentController {
    private final CommentsService commentService;

    public ApiCommentController(CommentsService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(path = "")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<NewCommentResponse> addNewComment(@RequestBody CommentRequest commentRequest,
                                                            Principal principal) {
        
        return commentService.addNewComment(commentRequest, principal);
    }
}
