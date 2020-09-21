package main.controller;

import main.api.requests.EditPostRequest;
import main.api.requests.LikeDislikeRequest;
import main.api.requests.PostRequest;
import main.api.responses.*;
import main.api.responses.post_responses.CertainPostResponse;
import main.api.responses.post_responses.EditPostResponse;
import main.api.responses.post_responses.NewPostResponse;
import main.api.responses.post_responses.PostResponse;
import main.services.PostsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {
    private final PostsService postsService;

    public ApiPostController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping(path = "")
    //@PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostResponse> listOfPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                    @RequestParam(defaultValue = "20", required = false) int limit,
                                                    @RequestParam(defaultValue = "recent", required = false) String mode )
    {
        return postsService.getPosts(offset, limit, mode);
    }

    @PostMapping(path = "")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<NewPostResponse> addNewPost(@RequestBody PostRequest postRequest,
                                                      Principal principal)
    {
        return postsService.addNewPost(postRequest, principal);
    }

    @GetMapping(path = "/search")
    //@PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostResponse> getPostsByQuery(@RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "20") int limit,
                                                        @RequestParam String query)
    {
        return postsService.findPostsByQuery(offset,limit,query);
    }

    @GetMapping(path = "/byDate")
    public ResponseEntity<PostResponse> getPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                       @RequestParam(defaultValue = "20") int limit,
                                                       @RequestParam String date)
    {
        return postsService.findPostsByDate(offset,limit,date);
    }

    @GetMapping(path = "/byTag")
    public ResponseEntity<PostResponse> getPostsByTag(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "20") int limit,
                                                      @RequestParam String tag)
    {
        return postsService.findPostsByTag(offset,limit,tag);
    }

    @GetMapping(path = "/{id}")
    public  ResponseEntity<CertainPostResponse> getPost(@PathVariable int id,
                                                        Principal principal)
    {
        return postsService.findPostById(id, principal);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public  ResponseEntity<EditPostResponse> editPost(@PathVariable int id,
                                                      @RequestBody EditPostRequest editPostRequest,
                                                      Principal principal)
    {
        return postsService.editPost(id, editPostRequest, principal);
    }

    @GetMapping(path = "/my")
    public  ResponseEntity<PostResponse> getUserPosts(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "20") int limit,
                                                      @RequestParam(defaultValue = "inactive") String status,
                                                      Principal principal)
    {
        if (principal == null){
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }
        return postsService.findUserPosts(offset, limit, status, principal);
    }




    @PostMapping(path= "/like")
    public ResponseEntity<LikeDislikeResponse> addNewLike(@RequestBody LikeDislikeRequest likeDislikeRequest,
                                                          Principal principal)
    {
        if (principal == null){
            LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
            likeDislikeResponse.setResult(false);
            return new ResponseEntity<>(likeDislikeResponse, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
        }

        return postsService.addNewLike(likeDislikeRequest, principal);
    }

    @PostMapping(path= "/dislike")
    public ResponseEntity<LikeDislikeResponse> addNewDislike(@RequestBody LikeDislikeRequest likeDislikeRequest,
                                                             Principal principal)
    {
        if (principal == null){
            LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
            likeDislikeResponse.setResult(false);
            return new ResponseEntity<>(likeDislikeResponse, HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
        }

        return postsService.addNewDislike(likeDislikeRequest, principal);
    }



}
