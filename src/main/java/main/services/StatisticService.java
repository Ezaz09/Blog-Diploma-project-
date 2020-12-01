package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.StatisticResponse;
import main.model.GlobalSetting;
import main.model.Post;
import main.model.User;
import main.model.repositories.GlobalSettingsRepository;
import main.model.repositories.PostsRepository;
import main.model.repositories.UsersRepository;
import main.api.mappers.StatisticMapper;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Slf4j
@Service
public class StatisticService {
    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public StatisticService(PostsRepository postsRepository,
                            UsersRepository usersRepository,
                            GlobalSettingsRepository globalSettingsRepository) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public ResponseEntity<StatisticResponse> collectInformationAboutUserPosts(Principal principal) {
        User user = usersRepository.findByEmail(principal.getName());
        List<Post> userPosts = postsRepository.getUserPosts(user);

        if (userPosts.size() == 0) {
            StatisticResponse statisticResponse = StatisticResponse.builder()
                    .postsCount(0)
                    .likesCount(0)
                    .dislikesCount(0)
                    .viewsCount(0)
                    .firstPublication(0L).build();
            return new ResponseEntity<>(statisticResponse, HttpStatus.OK);
        }

        StatisticResponse statisticResponse = new StatisticMapper().postsToStatisticResponse(userPosts);

        return new ResponseEntity<>(statisticResponse, HttpStatus.OK);
    }

    public ResponseEntity<StatisticResponse> collectInformationAboutAllPosts(Principal principal) throws Exception {
        List<GlobalSetting> globalSettings = globalSettingsRepository.findAll();
        GlobalSetting statisticIsPublic = globalSettings.stream()
                .filter(globalSetting -> "STATISTICS_IS_PUBLIC".equals(globalSetting.getName()))
                .findAny()
                .orElse(null);

        if (statisticIsPublic == null) {
            throw new HttpException("Blog statistics is not public!");
        }

        if (!statisticIsPublic.getValue().equals("true")) {
            if (principal == null) {
                throw new HttpException("Blog statistics is not public!");
            } else {
                User user = usersRepository.findByEmail(principal.getName());
                int isModerator = user.getIsModerator();
                if (isModerator == 0) {
                    throw new HttpException("Blog statistics is not public!");
                }
            }
        }

        List<Post> allPosts = postsRepository.findAll();

        if (allPosts.size() == 0) {
            StatisticResponse statisticResponse = StatisticResponse.builder()
                    .postsCount(0)
                    .likesCount(0)
                    .dislikesCount(0)
                    .viewsCount(0)
                    .firstPublication(0L).build();
            return new ResponseEntity<>(statisticResponse, HttpStatus.OK);
        }

        StatisticResponse statisticResponse = new StatisticMapper().postsToStatisticResponse(allPosts);

        return new ResponseEntity<>(statisticResponse, HttpStatus.OK);
    }
}
