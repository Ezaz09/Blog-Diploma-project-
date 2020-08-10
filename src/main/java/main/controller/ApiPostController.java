package main.controller;

import main.api.responses.*;
import main.services.CommentsService;
import main.services.PostsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiPostController {
    private PostsService postsService;
    private CommentsService commentsService;

    public ApiPostController(PostsService postsService, CommentsService commentsService) {
        this.postsService = postsService;
        this.commentsService = commentsService;
    }

    @GetMapping(path = "/api/post")
    public ResponseEntity<PostsResponse> listOfPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                                         @RequestParam(defaultValue = "20", required = false) int limit,
                                                                         @RequestParam(defaultValue = "recent", required = false) String mode )
    {
        return postsService.getPosts(offset, limit, mode);
    }

    @GetMapping(path = "/api/post/search")
    public ResponseEntity<PostsResponse> getPostsByQuery(@RequestParam(defaultValue = "0") int offset,
                                         @RequestParam(defaultValue = "20") int limit,
                                         @RequestParam String query)
    {
        return postsService.findPostsByQuery(offset,limit,query);
    }

    @GetMapping(path = "/api/post/byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                  @RequestParam(defaultValue = "20") int limit,
                                                  @RequestParam String date)
    {
        return postsService.findPostsByDate(offset,limit,date);
    }

    @GetMapping(path = "/api/post/byTag")
    public ResponseEntity<PostsResponse> getPostsByTag(@RequestParam(defaultValue = "0") int offset,
                                                            @RequestParam(defaultValue = "20") int limit,
                                                            @RequestParam String tag)
    {
        return postsService.findPostsByTag(offset,limit,tag);
    }

    @GetMapping(path = "/api/post/{id}")
    public ResponsePlatformApi getPost(@PathVariable int id)
    {
        return new ResponsePlatformApi();
    }


}
