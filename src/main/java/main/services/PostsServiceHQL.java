package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.PostsResponse;
import main.model.Posts;
import main.model.repositories.PostsRepositoryHQL;
import main.services.mappers.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Service
public class PostsServiceHQL {

    private final PostsRepositoryHQL postsRepository;
    private final PostMapper postMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    public PostsServiceHQL(PostsRepositoryHQL postsRepository, PostMapper postMapper) {
        this.postsRepository = postsRepository;
        this.postMapper = postMapper;
    }

    public List<PostsResponse> getPosts(String mode)
    {
        List<PostsResponse> allPosts = postsRepository.getAllPosts(mode);
       // List<PostsResponse> postsResponses = postMapper.postToPostResponse(allPosts);
        return  allPosts;
    }
}
