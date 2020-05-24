package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.PostsResponse;
import main.model.Posts;
import main.model.repositories.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    public List<PostsResponse> getPosts(int offset, int limit)
    {
        return postsRepository.getAllPosts(offset, limit);
    }
}
