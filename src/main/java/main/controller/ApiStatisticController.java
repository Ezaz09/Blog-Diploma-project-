package main.controller;

import main.api.responses.StatisticResponse;
import main.services.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/statistics")
public class ApiStatisticController {
    private final StatisticService statisticService;

    public ApiStatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping(path = "/my")
    public ResponseEntity<StatisticResponse> getUserPostsStatistics(Principal principal) {
        if (principal == null) {
            StatisticResponse statisticResponse = StatisticResponse.builder()
                    .postsCount(0)
                    .likesCount(0)
                    .dislikesCount(0)
                    .viewsCount(0)
                    .firstPublication(0L).build();
            return new ResponseEntity<>(statisticResponse, HttpStatus.OK);
        }

        return statisticService.collectInformationAboutUserPosts(principal);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<StatisticResponse> getAllPostsStatistics(Principal principal) throws Exception {
        return statisticService.collectInformationAboutAllPosts(principal);
    }
}
