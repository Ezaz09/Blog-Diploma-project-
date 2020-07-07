package main.controller;

import main.api.responses.CertainPostResponse;
import main.api.responses.CommentsResponse;
import main.api.responses.PostsResponse;
import main.api.responses.ResponsePlatformApi;
import main.services.CommentsService;
import main.services.PostsService;
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

    @GetMapping(path = "/api/post/")
    public ResponsePlatformApi listOfPosts(@RequestParam(defaultValue = "0") int offset,
                                           @RequestParam(defaultValue = "20") int limit)
    {
        List<PostsResponse> listOfPosts = postsService.getPosts(offset,limit);
        int total = listOfPosts.size();
        return new ResponsePlatformApi(total, listOfPosts);
    }

    @GetMapping(path = "/api/post/search/")
    public ResponsePlatformApi somePosts(@RequestParam(defaultValue = "0") int offset,
                                         @RequestParam(defaultValue = "20") int limit,
                                         @RequestParam String query)
    {
        List<PostsResponse> listOfPosts = postsService.getSomePosts(offset, limit, query);
        int total = listOfPosts.size();
        return new ResponsePlatformApi( total,listOfPosts);
    }

    @GetMapping(path = "/api/post/{id}")
    public CertainPostResponse getPost(@PathVariable int id)
    {
        CertainPostResponse certainPostResponse = new CertainPostResponse();
        PostsResponse certainPost = postsService.getCertainPost(id);
        certainPostResponse.setId(certainPost.getId());
        certainPostResponse.setTime(certainPost.getTime());
        certainPostResponse.setUser(certainPost.getUser());
        certainPostResponse.setTitle(certainPost.getTitle());
        certainPostResponse.setLikeCount(certainPost.getLikeCount());
        certainPostResponse.setDislikeCount(certainPost.getDislikeCount());
        certainPostResponse.setCommentCount(certainPost.getCommentCount());
        certainPostResponse.setViewCount(certainPost.getViewCount());
        List<CommentsResponse> allCommentsForCertainPost = commentsService.getAllCommentsForCertainPost(id);

        certainPostResponse.setComments(allCommentsForCertainPost);
        return certainPostResponse;
    }
}
