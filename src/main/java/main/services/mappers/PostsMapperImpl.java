package main.services.mappers;

import main.api.responses.PostDTO;
import main.api.responses.UserResponse;
import main.model.Posts;
import main.model.Users;

import java.util.ArrayList;
import java.util.List;

public class PostsMapperImpl {
    public List<PostDTO> postToPostResponse(List<Posts> posts) {
        if ( posts == null ) {
            return null;
        }

        List<PostDTO> list = new ArrayList<PostDTO>( posts.size() );
        for ( Posts posts1 : posts ) {
            list.add( postToPostDTO( posts1 ) );
        }

        return list;
    }

    public PostDTO postToPostDTO(Posts post) {
        if ( post == null ) {
            return null;
        }

        PostDTO postDTO = new PostDTO();

        postDTO.setId( post.getId() );
        if ( post.getTime() != null ) {
            postDTO.setTimestamp( post.getTime().getTime() ) ;
        }
        postDTO.setUser( usersToUserResponse( post.getUser() ) );
        postDTO.setTitle( post.getTitle() );
        postDTO.setLikeCount( post.getLikeVotes().size() );
        postDTO.setDislikeCount( post.getDislikeVotes().size() );
        postDTO.setViewCount( post.getViewCount() );
        postDTO.setCommentCount( post.getComments().size() );

        return postDTO;
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
