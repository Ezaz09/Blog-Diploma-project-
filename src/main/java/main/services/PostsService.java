package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.CertainPostResponse;
import main.api.responses.PostDTO;
import main.api.responses.PostsResponse;
import main.model.Posts;
import main.model.repositories.PostsRepository;
import main.services.mappers.PostsMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.*;
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
        if(mode.equals("recent"))
        {
            sort = Sort.by("time").descending();
        }
        else if(mode.equals("popular"))
        {
            sort = Sort.by(Sort.Order.desc("comments"));
                    //Sort.by("commentCount").descending();
        }
        else if(mode.equals("best"))
        {
            sort = Sort.by(Sort.Order.desc("likeVotes"));
                    //Sort.by("likeCount").descending();
        }
        else if(mode.equals("early"))
        {
            sort = Sort.by("time").ascending();
        }

        if(sort == null)
        {
            PostsResponse postsResponse = PostsResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
        }

        List<Posts> allPosts = postsRepository.findAll(PageRequest.of(offset, limit,sort)).getContent();
        List<PostDTO> listOfPosts = new PostsMapperImpl().postToPostResponse(allPosts);
        int total = listOfPosts.size();

        PostsResponse postsResponse = PostsResponse.builder()
                .count(total)
                    .posts(listOfPosts).build();
        return  new ResponseEntity<>(postsResponse, HttpStatus.OK);
    }

    public ResponseEntity<PostsResponse> findPostsByQuery(int offset, int limit, String query)
    {
        List<Posts> allPosts = postsRepository.getSomePosts(query);
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


        List<Posts> allPosts = postsRepository.getSomePostsByDate(parseDate);
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
        List<Posts> allPosts = postsRepository.getSomePostsByTag(tag);
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
