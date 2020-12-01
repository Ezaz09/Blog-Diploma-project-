package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.EditPostByModeratorRequest;
import main.api.requests.EditPostRequest;
import main.api.requests.LikeDislikeRequest;
import main.api.requests.PostRequest;
import main.api.responses.LikeDislikeResponse;
import main.api.responses.post_responses.*;
import main.model.GlobalSetting;
import main.model.Post;
import main.model.PostVote;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.model.repositories.PostVotesRepository;
import main.model.repositories.PostsRepository;
import main.model.repositories.UsersRepository;
import main.api.mappers.PostsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    private final UsersRepository usersRepository;

    private final PostVotesRepository postVotesRepository;

    private final TagsService tagsService;

    private final GlobalSettingsService globalSettingsService;

    @Autowired
    public PostsService(PostsRepository postsRepository,
                        UsersRepository usersRepository,
                        PostVotesRepository postVotesRepository,
                        TagsService tagsService,
                        GlobalSettingsService globalSettingsService) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.postVotesRepository = postVotesRepository;
        this.tagsService = tagsService;
        this.globalSettingsService = globalSettingsService;
    }

    public List<Post> getPosts(int offset,
                               int limit,
                               String mode) {
        List<Post> allPosts;
        switch (mode) {
            case "best":
                allPosts = postsRepository.getPostsSortByLikeVotes(PageRequest.of((offset / limit), limit));
                break;
            case "popular":
                allPosts = postsRepository.getPostsSortByComments(PageRequest.of((offset / limit), limit));
                break;
            case "early":
                allPosts = postsRepository.getPostsSortByDateAsc(PageRequest.of((offset / limit), limit));
                break;
            default:
                allPosts = postsRepository.getPostsSortByDateDesc(PageRequest.of((offset / limit), limit));
                break;
        }

        return allPosts;
    }

    public ResponseEntity<PostResponse> findPostsByQuery(int offset,
                                                         int limit,
                                                         String query) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> allPosts = postsRepository.getPostsByQuery(pageable, query);
        if (allPosts.size() == 0) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapper().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    public ResponseEntity<PostResponse> findPostsByDate(int offset,
                                                        int limit,
                                                        String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startOfDay;
        try {
            startOfDay = simpleDateFormat.parse(date);
        } catch (ParseException ex) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        Pageable pageable = PageRequest.of(offset, limit);

        LocalDateTime localDateTime = dateToLocalDateTime(startOfDay);
        LocalDateTime endOfDayLocalDate = localDateTime.with(LocalTime.MAX);
        Date endOfDay = localDateTimeToDate(endOfDayLocalDate);

        List<Post> allPosts = postsRepository.getPostsByDate(pageable, startOfDay, endOfDay);
        if (allPosts.size() == 0) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapper().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public ResponseEntity<PostResponse> findPostsByTag(int offset,
                                                       int limit,
                                                       String tag) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> allPosts = postsRepository.getPostsByTag(pageable, tag);
        if (allPosts.size() == 0) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapper().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    public Post findPostById(int id,
                             String userEmail) {
        User user = null;
        Post post = null;
        HashMap<String, Object> userAndPost = getUserAndPostAndCheckRelationshipOfUserAndPost(userEmail, id);

        if (userAndPost.containsKey("post")) {
            post = (Post) userAndPost.get("post");
        }

        if (userAndPost.containsKey("user")) {
            user = (User) userAndPost.get("user");
        }

        if (post == null) {
            return null;
        }

        setViewCountForPost(post, user);

        return post;
    }

    public ResponseEntity<PostResponse> findUserPosts(int offset,
                                                      int limit,
                                                      String mode,
                                                      Principal principal) {
        Pageable pageable = PageRequest.of(offset, limit);
        User user = usersRepository.findByEmail(principal.getName());

        List<Post> allPosts = null;
        switch (mode) {
            case "inactive":
                allPosts = postsRepository.getInactiveUserPosts(pageable, user);
                break;
            case "pending":
                allPosts = postsRepository.getPendingUserPosts(pageable, user);
                break;
            case "declined":
                allPosts = postsRepository.getDeclinedUserPosts(pageable, user);
                break;
            case "published":
                allPosts = postsRepository.getPublishedUserPosts(pageable, user);
                break;
        }

        if (allPosts.size() == 0) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapper().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    public ResponseEntity<PostResponse> findModeratorPosts(int offset,
                                                           int limit,
                                                           String status,
                                                           Principal principal) {

        ModerationStatus moderationStatus = null;
        switch (status) {
            case "new":
                moderationStatus = ModerationStatus.NEW;
                break;
            case "accepted":
                moderationStatus = ModerationStatus.ACCEPTED;
                break;
            case "declined":
                moderationStatus = ModerationStatus.DECLINED;
                break;
        }

        if (moderationStatus == null) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        Pageable pageable = PageRequest.of(offset, limit);
        User moderator = usersRepository.findByEmail(principal.getName());
        List<Post> moderatorPosts = postsRepository.getModeratorPosts(pageable, moderator, moderationStatus);
        if (moderatorPosts.size() == 0) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapper().postToPostResponse(moderatorPosts);
        int total = listOfPosts.size();
        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    public ResponseEntity<NewPostResponse> addNewPost(PostRequest postRequest,
                                                      Principal principal) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        User user = usersRepository.findByEmail(principal.getName());
        List<User> moderatorList = usersRepository.findModerator(pageRequest);

        int max = moderatorList.size() - 1;
        int rand = (int) (Math.random() * ++max);
        User moderator = moderatorList.get(rand);

        GlobalSetting postPremoderation = globalSettingsService.getGlobalSettingForAPIByName("POST_PREMODERATION");
        boolean needModeration = true;

        if(postPremoderation != null)
        {
            if(postPremoderation.getValue().equals("false")) {
                needModeration = false;
            } else {
                needModeration = true;
            }
        }

        Post newPost = new PostsMapper().postRequestToPost(postRequest,
                user,
                moderator,
                needModeration);

        if (newPost == null) {
            NewPostResponse newPostResponse = new NewPostResponse();
            newPostResponse.setResult(false);

            return new ResponseEntity<>(newPostResponse, HttpStatus.OK);
        }

        String editPostRequestTitle = postRequest.getTitle();
        String editPostRequestText = postRequest.getText();

        NewPostResponse newPostResponse = new NewPostResponse();

        HashMap<String, String> errors = checkParamsOfPost(editPostRequestText, editPostRequestTitle);

        if (!errors.isEmpty()) {
            newPostResponse.setResult(false);
            newPostResponse.setErrors(errors);
            return new ResponseEntity<>(newPostResponse, HttpStatus.OK);
        }

        postsRepository.save(newPost);
        tagsService.setTagsForNewPost(postRequest.getTags(),
                newPost.getId());


        newPostResponse.setResult(true);

        return new ResponseEntity<>(newPostResponse, HttpStatus.OK);
    }

    public ResponseEntity<EditPostResponse> editPost(int id,
                                                     EditPostRequest editPostRequest,
                                                     Principal principal) {
        User user = null;
        Post post = null;
        HashMap<String, Object> userAndPost = getUserAndPostAndCheckRelationshipOfUserAndPost(principal.getName(), id);

        if (userAndPost.containsKey("post")) {
            post = (Post) userAndPost.get("post");
        }

        if (userAndPost.containsKey("user")) {
            user = (User) userAndPost.get("user");
        }

        if (post == null) {
            EditPostResponse editPostResponse = new EditPostResponse();
            editPostResponse.setResult(false);


            HashMap<String, String> error = new HashMap<>();
            error.put("text", "Запрашиваемый пост не был найден!");

            editPostResponse.setErrors(error);

            return new ResponseEntity<>(editPostResponse, HttpStatus.OK);
        }

        String editPostRequestTitle = editPostRequest.getTitle();
        String editPostRequestText = editPostRequest.getText();

        EditPostResponse editPostResponse = new EditPostResponse();
        HashMap<String, String> errors = checkParamsOfPost(editPostRequestText, editPostRequestTitle);

        if (!errors.isEmpty()) {
            editPostResponse.setResult(false);
            editPostResponse.setErrors(errors);
            return new ResponseEntity<>(editPostResponse, HttpStatus.OK);
        }

        boolean changeStatus;

        assert user != null;
        changeStatus = user.getIsModerator() != 1;

        post = new PostsMapper().editPost(editPostRequest,
                post,
                changeStatus);

        postsRepository.save(post);
        tagsService.setTagsForEditingPost(editPostRequest.getTags(),
                post.getTags2Post(),
                post.getId());

        editPostResponse.setResult(true);

        return new ResponseEntity<>(editPostResponse, HttpStatus.OK);
    }

    private HashMap<String, String> checkParamsOfPost(String editPostRequestText,
                                                      String editPostRequestTitle) {
        HashMap<String, String> errors = new HashMap<>();
        if (editPostRequestTitle.length() < 3 ||
                editPostRequestText.length() < 50) {

            if (editPostRequestTitle.length() < 3) {
                errors.put("title", "Заголовок слишком короткий");
            }

            if (editPostRequestText.length() < 50) {
                errors.put("text", "Текст публикации слишком короткий");
            }
        }
        return errors;
    }

    public ResponseEntity<LikeDislikeResponse> addNewLike(LikeDislikeRequest likeDislikeRequest,
                                                          Principal principal) {
        User user = usersRepository.findByEmail(principal.getName());
        Post certainPost = postsRepository.getCertainPost(likeDislikeRequest.getPostId());

        if (certainPost == null) {
            LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
            likeDislikeResponse.setResult(false);
            return new ResponseEntity<>(likeDislikeResponse, HttpStatus.OK);
        }

        PostVote postVoteByUserId = postVotesRepository.findPostVoteByUserId(user.getId(), certainPost.getId());

        PostVote postVote;
        if (postVoteByUserId == null) {
            postVote = setNewLikeDislike(true,
                    certainPost.getId(),
                    user.getId());
            postVotesRepository.save(postVote);
        } else {
            if (postVoteByUserId.getValue() == 1) {
                LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
                likeDislikeResponse.setResult(false);

                return new ResponseEntity<>(likeDislikeResponse, HttpStatus.OK);
            }
            postVoteByUserId.setValue(1);
            postVotesRepository.save(postVoteByUserId);
        }

        LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
        likeDislikeResponse.setResult(true);

        return new ResponseEntity<>(likeDislikeResponse, HttpStatus.OK);
    }

    public ResponseEntity<LikeDislikeResponse> addNewDislike(LikeDislikeRequest likeDislikeRequest,
                                                             Principal principal) {
        User user = usersRepository.findByEmail(principal.getName());
        Post certainPost = postsRepository.getCertainPost(likeDislikeRequest.getPostId());

        PostVote postVoteByUserId = postVotesRepository.findPostVoteByUserId(user.getId(), certainPost.getId());

        PostVote postVote;
        if (postVoteByUserId == null) {
            postVote = setNewLikeDislike(false,
                    certainPost.getId(),
                    user.getId());
            postVotesRepository.save(postVote);
        } else {
            if (postVoteByUserId.getValue() == -1) {
                LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
                likeDislikeResponse.setResult(false);

                return new ResponseEntity<>(likeDislikeResponse, HttpStatus.OK);
            }
            postVoteByUserId.setValue(-1);
            postVotesRepository.save(postVoteByUserId);
        }

        LikeDislikeResponse likeDislikeResponse = new LikeDislikeResponse();
        likeDislikeResponse.setResult(true);

        return new ResponseEntity<>(likeDislikeResponse, HttpStatus.OK);
    }


    private PostVote setNewLikeDislike(boolean isLike,
                                         int postId,
                                         int userId) {
        PostVote postVote = new PostVote();
        postVote.setUserId(userId);
        postVote.setPostId(postId);
        postVote.setTime(new Date());

        if (isLike) {
            postVote.setValue(1);
        } else {
            postVote.setValue(0);
        }

        return postVote;
    }


    public ResponseEntity<EditPostByModeratorResponse> editPostByModerator(EditPostByModeratorRequest editPostByModeratorRequest,
                                                                           Principal principal) {
        User moderator = usersRepository.findByEmail(principal.getName());
        EditPostByModeratorResponse editPostByModeratorResponse = checkUser(moderator);

        if (!editPostByModeratorResponse.isResult()) {
            return new ResponseEntity<>(editPostByModeratorResponse, HttpStatus.OK);
        }

        Post post = postsRepository.getCertainPostForModerators(editPostByModeratorRequest.getPostId());

        if (post == null) {
            editPostByModeratorResponse = new EditPostByModeratorResponse();
            editPostByModeratorResponse.setResult(true);
            HashMap<String, String> error = new HashMap<>();
            editPostByModeratorResponse.setErrors(error);

            editPostByModeratorResponse.setResult(false);
            error.put("text", "Запрашиваемый пост не найден!");

            return new ResponseEntity<>(editPostByModeratorResponse, HttpStatus.OK);
        }

        if (editPostByModeratorRequest.getDecision().equals("accept")) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else if (editPostByModeratorRequest.getDecision().equals("decline")) {
            post.setModerationStatus(ModerationStatus.DECLINED);
        } else {
            editPostByModeratorResponse = new EditPostByModeratorResponse();
            editPostByModeratorResponse.setResult(true);
            HashMap<String, String> error = new HashMap<>();
            editPostByModeratorResponse.setErrors(error);

            editPostByModeratorResponse.setResult(false);
            error.put("text", "Выбранный статус для поста не распознан!");

            return new ResponseEntity<>(editPostByModeratorResponse, HttpStatus.OK);
        }

        post.setModerator(moderator);
        postsRepository.save(post);

        editPostByModeratorResponse = new EditPostByModeratorResponse();
        editPostByModeratorResponse.setResult(true);

        return new ResponseEntity<>(editPostByModeratorResponse, HttpStatus.OK);
    }

    private EditPostByModeratorResponse checkUser(User user) {
        EditPostByModeratorResponse editPostByModeratorResponse = new EditPostByModeratorResponse();
        editPostByModeratorResponse.setResult(true);
        HashMap<String, String> error = new HashMap<>();
        editPostByModeratorResponse.setErrors(error);

        if (user == null) {
            editPostByModeratorResponse.setResult(false);
            error.put("text", "Пользователь не авторизован!");
        } else if (user.getIsModerator() == 0) {
            editPostByModeratorResponse.setResult(false);
            error.put("text", "Пользователь не является модератором!");
        }

        return editPostByModeratorResponse;
    }

    public ResponseEntity<CountOfPostsPerYearResponse> getCountOfPostsPerYear(int year) {
        List<Post> posts = postsRepository.getCountOfPostsPerYear(year);

        CountOfPostsPerYearResponse countOfPostsPerYearResponse = new PostsMapper().postToCountOfPostsPerYear(posts, year);

        return new ResponseEntity<>(countOfPostsPerYearResponse, HttpStatus.OK);
    }

    private void setViewCountForPost(Post post,
                                     User user) {
        int viewCount = post.getViewCount();

        /**
          Проверим переданного пользователя
          если пользователь не является модератором и не являеться автором поста,
          то увеличим количество просмотров поста

          если пользователь не авторизован (null), то тоже увеличим количество просмотров
         */

        if (user != null) {
            if (user.getIsModerator() != 1
                    && user.getId() != post.getUser().getId()) {
                post.setViewCount(post.getViewCount() + 1);
            }
        } else {
            post.setViewCount(post.getViewCount() + 1);
        }

        if (viewCount != post.getViewCount()) {
            postsRepository.save(post);
        }

    }

    private HashMap<String, Object> getUserAndPostAndCheckRelationshipOfUserAndPost(String userEmail,
                                                                                    int id) {
        HashMap<String, Object> results = new HashMap<>();

        /**
          Получим пользователя для проверки поста
         */

        User user = null;
        if (userEmail != null) {
            user = usersRepository.findByEmail(userEmail);
        }

        /**
          В случае если пользователь был найден и не является модератором
          проверим, являеться ли полученный пользователь
          автором поста, для случаев когда автор поста захочет
          посмотреть/внести изменения в свой пост
          в момент когда пост еще не одобрен/отклонен модератором

          Если пользователь не был найден - значит запрос поста приходит с главной страницы
          где посты могут просматривать не авторизованные пользователи
         */

        Post post;
        if (user != null && user.getIsModerator() == 1) {
            post = postsRepository.getCertainPostForModerators(id);
        } else if (user != null && user.getIsModerator() != 1) {
            post = postsRepository.getCertainPostForModerators(id);

            if (post.getModerationStatus() != ModerationStatus.ACCEPTED) {
                if (post.getUser() != user) {
                    post = null;
                }
            }
        } else {
            post = postsRepository.getCertainPost(id);
        }


        results.put("post", post);
        if (user != null) {
            results.put("user", user);
        }


        return results;
    }
}

