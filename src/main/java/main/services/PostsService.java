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
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PostsService {

    private final PostsRepository postsRepository;

    @Autowired
    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    public List<PostDTO> getPosts(int offset, int limit, String mode)
    {
        Pageable sortedBy = null;
        if(mode.equals("recent"))
        {
            sortedBy = PageRequest.of(offset, limit, Sort.by("time").descending());
        }
        else if(mode.equals("popular"))
        {
            sortedBy = PageRequest.of(offset, limit, Sort.by("commentCount").descending());
        }
        else if(mode.equals("best"))
        {
            sortedBy = PageRequest.of(offset, limit, Sort.by("likeCount").descending());
        }
        else if(mode.equals("early"))
        {
            sortedBy = PageRequest.of(offset, limit, Sort.by("time").ascending());
        }

        List<Posts> allPosts = postsRepository.findAll(sortedBy).getContent();
        List<PostDTO> postsResponse = new PostsMapperImpl().postToPostResponse(allPosts);
        return  postsResponse;
    }

}
