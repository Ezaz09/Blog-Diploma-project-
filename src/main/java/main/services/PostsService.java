package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.PostDTO;
import main.api.responses.PostsResponse;
import main.model.Posts;
import main.model.repositories.PostsRepository;
import main.services.mappers.PostsMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    public ResponseEntity<PostsResponse> getPosts(int offset, int limit, String mode)
    {
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

        if(sort == null)
        {
            PostsResponse postsResponse = PostsResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
        }

        List<Posts> allPosts;
        if(mode.equals("best"))
        {
            allPosts = postsRepository.getPostsSortByLikeVotes(PageRequest.of(offset,limit));
        }
        else if(mode.equals("popular"))
        {
            allPosts = postsRepository.getPostsSortByComments(PageRequest.of(offset,limit));
        }
        else
        {
            allPosts = postsRepository.findAll(PageRequest.of(offset, limit, sort)).getContent();
        }

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
        int total = listOfPosts.size();

        PostsResponse postsResponse = PostsResponse.builder()
                .count(total)
                    .posts(listOfPosts).build();
        return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    public ResponseEntity<PostsResponse> findPostsByQuery(int offset, int limit, String query)
    {
        List<Posts> allPosts = postsRepository.getPostsByQuery(query);
        if(allPosts.size() == 0)
        {
            PostsResponse postsResponse = PostsResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostsResponse postsResponse = PostsResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    public ResponseEntity<PostsResponse> findPostsByDate(int offset, int limit, String date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date parseDate;
           try
           {
               parseDate = simpleDateFormat.parse(date);
           }
           catch (ParseException ex)
           {
               PostsResponse postsResponse = PostsResponse.builder()
                       .count(0)
                       .posts(Collections.emptyList()).build();
               return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
           }


        List<Posts> allPosts = postsRepository.getPostsByDate(parseDate);
        if(allPosts.size() == 0)
        {
            PostsResponse postsResponse = PostsResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostsResponse postsResponse = PostsResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    public ResponseEntity<PostsResponse> findPostsByTag(int offset, int limit, String tag)
    {
        List<Posts> allPosts = postsRepository.getPostsByTag(tag);
        if(allPosts.size() == 0)
        {
            PostsResponse postsResponse = PostsResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
        }

        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
        int total = listOfPosts.size();
        PostsResponse postsResponse = PostsResponse.builder()
                .count(total)
                .posts(listOfPosts).build();
        return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }
}
