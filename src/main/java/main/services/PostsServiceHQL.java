package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.PostsResponse;
import main.api.responses.PostsResponseHQL;
import main.api.responses.UserResponse;
import main.model.Posts;
import main.model.Users;
import main.model.repositories.PostsRepositoryHQL;
import main.services.mappers.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public List<PostsResponseHQL> getPosts(int offset, int limit, String mode)
    {

        Pageable sortedByDate = PageRequest.of(offset, limit, Sort.by("time").descending());
        List<Posts> allPosts = postsRepository.findAll(sortedByDate).getContent();
        List<PostsResponseHQL> postsResponses = postToPostResponse(allPosts);
        return  postsResponses;
    }



    public List<PostsResponseHQL> postToPostResponse(List<Posts> posts) {
        if ( posts == null ) {
            return null;
        }

        List<PostsResponseHQL> list = new ArrayList<PostsResponseHQL>( posts.size() );
        for ( Posts posts1 : posts ) {
            list.add( postToPostResponse( posts1 ) );
        }

        return list;
    }

    public PostsResponseHQL postToPostResponse(Posts post) {
        if ( post == null ) {
            return null;
        }

        PostsResponseHQL postsResponseHQL = new PostsResponseHQL();

        postsResponseHQL.setId( post.getId() );
        if ( post.getTime() != null ) {
            postsResponseHQL.setTime( DateTimeFormatter.ISO_LOCAL_DATE.format( post.getTime() ) );
        }
        postsResponseHQL.setUser( usersToUserResponse( post.getUser() ) );
        postsResponseHQL.setTitle( post.getTitle() );
        postsResponseHQL.setLikeCount( post.getLikeVotes().size() );
        postsResponseHQL.setDislikeCount( post.getDislikeVotes().size() );
        postsResponseHQL.setViewCount( post.getViewCount() );
        postsResponseHQL.setCommentCount( post.getComments().size() );

        return postsResponseHQL;
    }

    protected UserResponse usersToUserResponse(Users users) {
        if ( users == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId( users.getId() );
        userResponse.setName( users.getName() );

        return userResponse;
    }
}
