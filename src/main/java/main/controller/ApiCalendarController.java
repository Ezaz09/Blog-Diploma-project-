package main.controller;


import main.api.responses.post_responses.CountOfPostsPerYearResponse;
import main.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Calendar;

@RestController
@RequestMapping("/api/calendar")
public class ApiCalendarController {
    private final PostsService postsService;

    @Autowired
    public ApiCalendarController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping(path = "")
    public ResponseEntity<CountOfPostsPerYearResponse> countOfPostsPerYear(@RequestParam(defaultValue = "0", required = false) int year) throws ParseException {
        if(year == 0)
        {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        return postsService.getCountOfPostsPerYear(year);
    }
}
