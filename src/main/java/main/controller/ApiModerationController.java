package main.controller;

import main.api.requests.EditPostByModeratorRequest;
import main.api.responses.post_responses.EditPostByModeratorResponse;
import main.api.responses.post_responses.PostResponse;
import main.services.PostsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;

@RestController
public class ApiModerationController {

    private final PostsService postsService;

    public ApiModerationController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping(path = "/api/post/moderation")
    public ResponseEntity<PostResponse> getModeratorPosts(@RequestParam(defaultValue = "0") int offset,
                                                          @RequestParam(defaultValue = "20") int limit,
                                                          @RequestParam(defaultValue = "new", required = false) String status,
                                                          Principal principal) {
        if (principal == null) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        return postsService.findModeratorPosts(offset, limit, status, principal);
    }

    @PostMapping(path = "/api/moderation")
    public ResponseEntity<EditPostByModeratorResponse> editUserPost(@RequestBody EditPostByModeratorRequest editPostByModeratorRequest,
                                                                    Principal principal) {
        return postsService.editPostByModerator(editPostByModeratorRequest, principal);
    }
}
