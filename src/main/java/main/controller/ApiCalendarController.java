package main.controller;


import main.api.mappers.PostsMapper;
import main.api.responses.post_responses.CountOfPostsPerYearResponse;
import main.model.Post;
import main.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class ApiCalendarController {
    private final PostsService postsService;

    private final PostsMapper postsMapper;

    @Autowired
    public ApiCalendarController(PostsService postsService,
                                 PostsMapper postsMapper) {
        this.postsService = postsService;
        this.postsMapper = postsMapper;
    }

    @GetMapping(path = "")
    public ResponseEntity<CountOfPostsPerYearResponse> countOfPostsPerYear(@RequestParam(defaultValue = "0", required = false) int year){
        if (year == 0) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        List<Post> posts = postsService.getCountOfPostsPerYear(year);

        CountOfPostsPerYearResponse countOfPostsPerYearResponse = new PostsMapper().postToCountOfPostsPerYear(posts, year);
        return new ResponseEntity<>(countOfPostsPerYearResponse, HttpStatus.OK);
    }
}
