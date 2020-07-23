package main.controller;

import main.api.responses.*;
import main.model.Posts;
import main.services.CommentsService;
import main.services.PostsService;
import main.services.PostsServiceHQL;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiPostController {
    private PostsService postsService;
    private PostsServiceHQL postsServiceHQL;
    private CommentsService commentsService;

    public ApiPostController(PostsService postsService, PostsServiceHQL postsServiceHQL, CommentsService commentsService) {
        this.postsService = postsService;
        this.commentsService = commentsService;
        this.postsServiceHQL = postsServiceHQL;
    }

    @GetMapping(path = "/api/post/")
    public ResponsePlatformApi listOfPosts(@RequestParam(defaultValue = "0") int offset,
                                           @RequestParam(defaultValue = "20") int limit,
                                           @RequestParam String mode)
    {
        List<PostsResponseHQL> listOfPosts = postsServiceHQL.getPosts(offset, limit, mode);
                //postsService.getPosts(offset,limit, mode);
        int total = listOfPosts.size();
        return new ResponsePlatformApi(total, listOfPosts);
    }

    @GetMapping(path = "/api/post/search/")
    public ResponsePlatformApi getSomePosts(@RequestParam(defaultValue = "0") int offset,
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
        if(certainPost == null)
        {
            return certainPostResponse;
        }
        certainPostResponse.setId(certainPost.getId());
        certainPostResponse.setTime(certainPost.getTime());
        certainPostResponse.setUser(certainPost.getUser());
        certainPostResponse.setTitle(certainPost.getTitle());
        certainPostResponse.setLikeCount(certainPost.getLikeCount());
        certainPostResponse.setDislikeCount(certainPost.getDislikeCount());
        certainPostResponse.setCommentCount(certainPost.getCommentCount());
        certainPostResponse.setViewCount(certainPost.getViewCount());
        List<CommentsResponse> allCommentsForCertainPost = commentsService.getAllCommentsForCertainPost(id);
        if(allCommentsForCertainPost == null)
        {
            return certainPostResponse;
        }
        certainPostResponse.setComments(allCommentsForCertainPost);
        return certainPostResponse;
    }

    @GetMapping(path = "/api/post/byDate/")
    public ResponsePlatformApi getSomePostsByDate(@RequestParam(defaultValue = "0") int offset,
                                         @RequestParam(defaultValue = "20") int limit,
                                         @RequestParam String date)
    {
        List<PostsResponse> listOfPosts = postsService.getSomePostsByDate(offset, limit, date);
        int total = listOfPosts.size();
        return new ResponsePlatformApi(total,listOfPosts);
    }
}
