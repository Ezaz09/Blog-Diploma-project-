package main.controller;

import main.api.mappers.PostsMapper;
import main.api.requests.EditPostByModeratorRequest;
import main.api.responses.post_responses.EditPostByModeratorResponse;
import main.api.DTO.PostDTO;
import main.api.responses.post_responses.PostResponse;
import main.model.Post;
import main.model.User;
import main.services.PostsService;
import main.services.ResponseService;
import main.services.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
public class ApiModeratorController {

    private final PostsService postsService;

    private final SearchService searchService;

    private final ResponseService responseService;

    public ApiModeratorController(PostsService postsService,
                                  SearchService searchService,
                                  ResponseService responseService) {
        this.postsService = postsService;
        this.searchService = searchService;
        this.responseService = responseService;
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

        List<Post> moderatorPosts = postsService.findModeratorPosts(offset, limit, status, principal.getName());

        List<PostDTO> listOfPosts = new PostsMapper().postToPostResponse(moderatorPosts);
        PostResponse postResponse = PostResponse.builder()
                .count(listOfPosts.size())
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @PostMapping(path = "/api/moderation")
    public ResponseEntity<EditPostByModeratorResponse> editUserPost(@RequestBody EditPostByModeratorRequest editPostByModeratorRequest,
                                                                    Principal principal) {
        EditPostByModeratorResponse editPostByModeratorResponse = responseService.createNewEditPostByModeratorResponse();

        if(principal == null) {
            editPostByModeratorResponse.getErrors().put("auth", "Пользователь не авторизован!");
            return new ResponseEntity<>(editPostByModeratorResponse, HttpStatus.OK);
        }

        User user = searchService.findUserByEmail(principal.getName());

        checkUserOnNullAndCheckIfUserModerator(user, editPostByModeratorResponse);

        if(editPostByModeratorResponse.getErrors().size() != 0) {
            return new ResponseEntity<>(editPostByModeratorResponse, HttpStatus.OK);
        }

        HashMap<String, String> mapOfErrors = postsService.editPostByModerator(editPostByModeratorRequest.getPostId(),
                                                                               editPostByModeratorRequest.getDecision(),
                                                                               user);

        if(!mapOfErrors.isEmpty()) {
           editPostByModeratorResponse.setResult(false);
           editPostByModeratorResponse.setErrors(mapOfErrors);
        }

        return new ResponseEntity<>(editPostByModeratorResponse, HttpStatus.OK);
    }

    private void checkUserOnNullAndCheckIfUserModerator(User user,
                                                        EditPostByModeratorResponse editPostByModeratorResponse) {
        HashMap<String, String> errors = editPostByModeratorResponse.getErrors();

        if (user == null) {
            editPostByModeratorResponse.setResult(false);
            errors.put("text", "Пользователь не был найден!");
        } else if (user.getIsModerator() == 0) {
            editPostByModeratorResponse.setResult(false);
            errors.put("text", "Пользователь не является модератором!");
        }

    }

}
