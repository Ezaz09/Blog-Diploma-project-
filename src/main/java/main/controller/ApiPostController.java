package main.controller;

import main.api.responses.*;
import main.services.CommentsService;
import main.services.PostsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;

@RestController
public class ApiPostController {
    private PostsService postsService;
    private CommentsService commentsService;

    public ApiPostController(PostsService postsService, CommentsService commentsService) {
        this.postsService = postsService;
        this.commentsService = commentsService;
    }

    @GetMapping(path = "/api/post")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostResponse> listOfPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                    @RequestParam(defaultValue = "20", required = false) int limit,
                                                    @RequestParam(defaultValue = "recent", required = false) String mode )
    {
        return postsService.getPosts(offset, limit, mode);
    }

    @GetMapping(path = "/api/post/search")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostResponse> getPostsByQuery(@RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "20") int limit,
                                                        @RequestParam String query)
    {
        return postsService.findPostsByQuery(offset,limit,query);
    }

    @GetMapping(path = "/api/post/byDate")
    public ResponseEntity<PostResponse> getPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                       @RequestParam(defaultValue = "20") int limit,
                                                       @RequestParam String date)
    {
        return postsService.findPostsByDate(offset,limit,date);
    }

    @GetMapping(path = "/api/post/byTag")
    public ResponseEntity<PostResponse> getPostsByTag(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "20") int limit,
                                                      @RequestParam String tag)
    {
        return postsService.findPostsByTag(offset,limit,tag);
    }

    @GetMapping(path = "/api/post/{id}")
    public  ResponseEntity<CertainPostResponse> getPost(@PathVariable int id)
    {
        return postsService.findPostById(id);
    }

    @GetMapping(path = "/api/post/my")
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

    @GetMapping(path = "/api/post/moderation")
    public  ResponseEntity<PostResponse> getModeratorPosts(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "20") int limit,
                                                      @RequestParam(defaultValue = "new", required = false)String status,
                                                      Principal principal)
    {
        if (principal == null){
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }
        return postsService.findModeratorPosts(offset, limit, status, principal);
    }


}
