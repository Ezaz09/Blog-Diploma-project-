package main.controller;

import main.api.mappers.PostsMapper;
import main.api.requests.EditPostRequest;
import main.api.requests.LikeDislikeRequest;
import main.api.requests.PostRequest;
import main.api.responses.LikeDislikeResponse;
import main.api.responses.post_responses.*;
import main.model.Post;
import main.model.repositories.PostsRepository;
import main.model.repositories.UsersRepository;
import main.services.PostsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {
    private final PostsService postsService;
    private final PostsMapper postsMapper;
    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;

    public ApiPostController(PostsService postsService,
                             PostsMapper postsMapper,
                             PostsRepository postsRepository,
                             UsersRepository usersRepository) {
        this.postsService = postsService;
        this.postsMapper = postsMapper;
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
    }

    @GetMapping(path = "")
    public ResponseEntity<PostResponse> listOfPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                    @RequestParam(defaultValue = "20", required = false) int limit,
                                                    @RequestParam(defaultValue = "recent", required = false) String mode) {
        boolean isModePassed = checkGetPostsRequest(mode);

        if(!isModePassed) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<Post> posts = postsService.getPosts(offset, limit, mode);

        List<PostDTO> listOfPosts = postsMapper.postToPostResponse(posts);
        int total = listOfPosts.size();

        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    private boolean checkGetPostsRequest(String mode) {
        return mode.equals("best")
                || mode.equals("popular")
                || mode.equals("early")
                || mode.equals("recent");
    }

    @PostMapping(path = "")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<NewPostResponse> addNewPost(@RequestBody PostRequest postRequest,
                                                      Principal principal) {
        return postsService.addNewPost(postRequest, principal);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<PostResponse> getPostsByQuery(@RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "20") int limit,
                                                        @RequestParam String query) {
        return postsService.findPostsByQuery(offset, limit, query);
    }

    @GetMapping(path = "/byDate")
    public ResponseEntity<PostResponse> getPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                       @RequestParam(defaultValue = "20") int limit,
                                                       @RequestParam String date) {
        return postsService.findPostsByDate(offset, limit, date);
    }

    @GetMapping(path = "/byTag")
    public ResponseEntity<PostResponse> getPostsByTag(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "20") int limit,
                                                      @RequestParam String tag) {
        return postsService.findPostsByTag(offset, limit, tag);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CertainPostResponse> getPost(@PathVariable int id,
                                                       Principal principal) {
        String userEmail;
        if(principal != null) {
            userEmail = principal.getName();
        } else {
            userEmail = null;
        }

        Post postById = postsService.findPostById(id, userEmail);

        if (postById == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        CertainPostResponse certainPostResponse;
        certainPostResponse = postsMapper.certainPostToPostResponse(postById);

        return new ResponseEntity<>(certainPostResponse, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<EditPostResponse> editPost(@PathVariable int id,
                                                     @RequestBody EditPostRequest editPostRequest,
                                                     Principal principal) {
        return postsService.editPost(id, editPostRequest, principal);
    }

    @GetMapping(path = "/my")
    public ResponseEntity<PostResponse> getUserPosts(@RequestParam(defaultValue = "0") int offset,
                                                     @RequestParam(defaultValue = "20") int limit,
                                                     @RequestParam(defaultValue = "inactive") String status,
                                                     Principal principal) {
        if (principal == null) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }
        return postsService.findUserPosts(offset, limit, status, principal);
    }


    @PostMapping(path = "/like")
    public ResponseEntity<LikeDislikeResponse> addNewLike(@RequestBody LikeDislikeRequest likeDislikeRequest,
                                                          Principal principal) {
        if (principal == null) {
            LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
            likeDislikeResponse.setResult(false);
            return new ResponseEntity<>(likeDislikeResponse, HttpStatus.OK);
        }

        return postsService.addNewLike(likeDislikeRequest, principal);
    }

    @PostMapping(path = "/dislike")
    public ResponseEntity<LikeDislikeResponse> addNewDislike(@RequestBody LikeDislikeRequest likeDislikeRequest,
                                                             Principal principal) {
        if (principal == null) {
            LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
            likeDislikeResponse.setResult(false);
            return new ResponseEntity<>(likeDislikeResponse, HttpStatus.OK);
        }

        return postsService.addNewDislike(likeDislikeRequest, principal);
    }
}
