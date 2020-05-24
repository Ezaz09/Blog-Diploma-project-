package main.controller;

import main.api.responses.PostsResponse;
import main.api.responses.ResponsePlatformApi;
import main.model.Posts;
import main.services.PostsService;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiPostController {
    private PostsService postsService;

    public ApiPostController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping(path = "/api/post/")
    public ResponsePlatformApi listOfPosts(@RequestParam(defaultValue = "0") int offset,
                                           @RequestParam(defaultValue = "20") int limit)
    {
        List<PostsResponse> listOfPosts = postsService.getPosts(offset,limit);
        int total = listOfPosts.size();
        return new ResponsePlatformApi("done", total, offset,limit,listOfPosts);
    }
}
