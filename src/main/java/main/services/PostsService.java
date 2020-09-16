package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.requests.EditPostRequest;
import main.api.requests.LikeDislikeRequest;
import main.api.requests.PostRequest;
import main.api.responses.*;
import main.model.*;
import main.model.enums.ModerationStatus;
import main.model.repositories.*;
import main.services.mappers.PostsMapperImpl;
import main.services.mappers.TagsMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    private final UserRepository userRepository;

    private final PostVotesRepository postVotesRepository;

    private final TagsService tagsService;


    @Autowired
    public PostsService(PostsRepository postsRepository,
                        UserRepository userRepository,
                        PostVotesRepository postVotesRepository,
                        TagsService tagsService) {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
        this.postVotesRepository = postVotesRepository;
        this.tagsService = tagsService;
    }

    public ResponseEntity<PostResponse> getPosts(int offset,
                                                 int limit,
                                                 String mode) {
        Sort sort = null;
        switch (mode) {
            case "recent":
                sort = Sort.by("time").descending();
                break;
            case "popular":
                sort = Sort.by(Sort.Order.desc("comments"));
                break;
            case "best":
                sort = Sort.by(Sort.Order.desc("likeVotes"));
                break;
            case "early":
                sort = Sort.by("time").ascending();
                break;
        }

        if (sort == null) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<Post> allPosts;
        if (mode.equals("best")) {
            allPosts = postsRepository.getPostsSortByLikeVotes(PageRequest.of((offset / limit), limit));
        } else if (mode.equals("popular")) {
            allPosts = postsRepository.getPostsSortByComments(PageRequest.of((offset / limit), limit));
        } else {
            allPosts = postsRepository.findAll(PageRequest.of(offset, limit, sort)).getContent();
        }

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
        int total = listOfPosts.size();

        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
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

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
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
        Date parseDate;
        try {
            parseDate = simpleDateFormat.parse(date);
        } catch (ParseException ex) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> allPosts = postsRepository.getPostsByDate(pageable, parseDate);
        if (allPosts.size() == 0) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
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

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    public ResponseEntity<CertainPostResponse> findPostById(int id,
                                                            Principal principal) {
        Post post = postsRepository.getCertainPost(id);
        if (post == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        int viewCount = post.getViewCount();
        if( principal !=null )
        {
            User user = userRepository.findByEmail(principal.getName());
            if(user.getIsModerator() != 1
               && user.getId() != post.getUser().getId())
            {
                post.setViewCount(post.getViewCount() + 1);

            }
        }
        else
        {
            post.setViewCount(post.getViewCount() + 1);
        }

        CertainPostResponse certainPostResponse;
        if( viewCount != post.getViewCount())
        {
            Post savedPost = postsRepository.save(post);
            certainPostResponse = new PostsMapperImpl().certainPostToPostResponse(savedPost);
        }
        else
        {
            certainPostResponse = new PostsMapperImpl().certainPostToPostResponse(post);
        }

        return new ResponseEntity<>(certainPostResponse, HttpStatus.OK);
    }

    public ResponseEntity<PostResponse> findUserPosts(int offset,
                                                      int limit,
                                                      String mode,
                                                      Principal principal) {
        Pageable pageable = PageRequest.of(offset, limit);
        User user = userRepository.findByEmail(principal.getName());

        List<Post> allPosts = null;
        switch (mode){
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

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
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

        if (moderationStatus == null)
        {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        Pageable pageable = PageRequest.of(offset, limit);
        User moderator = userRepository.findByEmail(principal.getName());
        List<Post> moderatorPosts = postsRepository.getModeratorPosts(pageable, moderator, moderationStatus);
        if (moderatorPosts.size() == 0) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(moderatorPosts);
        int total = listOfPosts.size();
        PostResponse postResponse = PostResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    public ResponseEntity<NewPostResponse> addNewPost(PostRequest postRequest,
                                                      Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        User moderator = userRepository.findModerator();

        Post newPost = new PostsMapperImpl().postRequestToPost(postRequest,
                user,
                moderator);

        if( newPost == null )
        {
            NewPostResponse newPostResponse = new NewPostResponse();
            newPostResponse.setResult(false);

            return new ResponseEntity<>(newPostResponse, HttpStatus.OK);
        }

        String editPostRequestTitle = postRequest.getTitle();
        String editPostRequestText = postRequest.getText();

        if(editPostRequestTitle.length() < 3 ||
                editPostRequestText.length() < 50)
        {
            NewPostResponse newPostResponse = new NewPostResponse();
            newPostResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>();

            if(editPostRequestTitle.length() < 3)
            {
                errors.put("title", "Заголовок слишком короткий");
            }

            if(editPostRequestText.length() < 50)
            {
                errors.put("text", "Текст публикации слишком короткий");
            }

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrors(errors);

            newPostResponse.setErrors(errorResponse);
            return new ResponseEntity<>(newPostResponse, HttpStatus.OK);
        }

        postsRepository.save(newPost);
        tagsService.setTagsForNewPost(postRequest.getTags(),
                                      newPost.getId());

        NewPostResponse newPostResponse = new NewPostResponse();
        newPostResponse.setResult(true);

        return new ResponseEntity<>(newPostResponse, HttpStatus.OK);
    }

    public ResponseEntity<EditPostResponse> editPost(int id,
                                                     EditPostRequest editPostRequest,
                                                     Principal principal)
    {
        Post post = postsRepository.getCertainPost(id);

        if (post == null) {
            EditPostResponse editPostResponse = new EditPostResponse();
            editPostResponse.setResult(false);


            HashMap<String, String> error = new HashMap<>();
            error.put("text", "Запрашиваемый пост не был найден!");

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrors(error);

            editPostResponse.setErrors(errorResponse);

            return new ResponseEntity<>(editPostResponse, HttpStatus.OK);
        }

        String editPostRequestTitle = editPostRequest.getTitle();
        String editPostRequestText = editPostRequest.getText();

        if(editPostRequestTitle.length() < 3 ||
                editPostRequestText.length() < 50)
        {
            EditPostResponse editPostResponse = new EditPostResponse();
            editPostResponse.setResult(false);

            HashMap<String, String> errors = new HashMap<>();

            if(editPostRequestTitle.length() < 3)
            {
                errors.put("title", "Заголовок слишком короткий");
            }

            if(editPostRequestText.length() < 50)
            {
                errors.put("text", "Текст публикации слишком короткий");
            }

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrors(errors);

            editPostResponse.setErrors(errorResponse);
            return new ResponseEntity<>(editPostResponse, HttpStatus.OK);
        }

        User user = userRepository.findByEmail(principal.getName());
        boolean changeStatus;

        if(user.getIsModerator() == 1)
        {
            changeStatus = false;
        }
        else
        {
            changeStatus = true;
        }

        post = new PostsMapperImpl().editPost(editPostRequest,
                post,
                changeStatus);

        postsRepository.save(post);
        tagsService.setTagsForEditingPost(editPostRequest.getTags(),
                                          post.getTags2Post(),
                                          post.getId());

        EditPostResponse editPostResponse = new EditPostResponse();
        editPostResponse.setResult(true);

        return new ResponseEntity<>(editPostResponse, HttpStatus.OK);
    }

    public ResponseEntity<LikeDislikeResponse> addNewLike(LikeDislikeRequest likeDislikeRequest,
                                                          Principal principal)
    {
        User user = userRepository.findByEmail(principal.getName());
        Post certainPost = postsRepository.getCertainPost(likeDislikeRequest.getPostId());

        PostVote postVoteByUserId = postVotesRepository.findPostVoteByUserId(user.getId(), certainPost.getId());

        PostVote postVote;
        if(postVoteByUserId == null)
        {
            postVote = setNewLikeDislike(true,
                                                   certainPost.getId(),
                                                   user.getId());
            postVotesRepository.save(postVote);
        }
        else
        {
            if( postVoteByUserId.getValue() == 1)
            {
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
                                                             Principal principal)
    {
        User user = userRepository.findByEmail(principal.getName());
        Post certainPost = postsRepository.getCertainPost(likeDislikeRequest.getPostId());

        PostVote postVoteByUserId = postVotesRepository.findPostVoteByUserId(user.getId(), certainPost.getId());

        PostVote postVote;
        if(postVoteByUserId == null)
        {
            postVote = setNewLikeDislike(false,
                    certainPost.getId(),
                    user.getId());
            postVotesRepository.save(postVote);
        }
        else
        {
            if( postVoteByUserId.getValue() == -1)
            {
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



    protected PostVote setNewLikeDislike(boolean isLike,
                                         int postId,
                                         int userId)
    {
        PostVote postVote = new PostVote();
        postVote.setUserId(userId);
        postVote.setPostId(postId);
        postVote.setTime(new Date());

        if(isLike)
        {
            postVote.setValue(1);
        }
        else
        {
            postVote.setValue(0);
        }

        return postVote;
    }



}

