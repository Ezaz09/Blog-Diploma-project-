package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.CertainPostResponse;
import main.api.responses.PostDTO;
import main.api.responses.PostResponse;
import main.model.Post;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.model.repositories.PostsRepository;
import main.model.repositories.UserRepository;
import main.services.mappers.PostsMapperImpl;
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

    @Autowired
    public PostsService(PostsRepository postsRepository, UserRepository userRepository) {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<PostResponse> getPosts(int offset, int limit, String mode) {
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

    public ResponseEntity<PostResponse> findPostsByQuery(int offset, int limit, String query) {
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

    public ResponseEntity<PostResponse> findPostsByDate(int offset, int limit, String date) {
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

    public ResponseEntity<PostResponse> findPostsByTag(int offset, int limit, String tag) {
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

    public ResponseEntity<CertainPostResponse> findPostById(int id) {
        Post post = postsRepository.getCertainPost(id);
        if (post == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        CertainPostResponse certainPostResponse = new PostsMapperImpl().certainPostToPostResponse(post);
        return new ResponseEntity<>(certainPostResponse, HttpStatus.OK);
    }

    public ResponseEntity<PostResponse> findUserPosts(int offset, int limit, String mode, Principal principal) {
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

    public ResponseEntity<PostResponse> findModeratorPosts(int offset, int limit, String status, Principal principal) {

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

}

