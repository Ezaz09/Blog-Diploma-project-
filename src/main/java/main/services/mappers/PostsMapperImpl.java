package main.services.mappers;

import main.api.responses.CertainPostResponse;
import main.api.responses.CommentResponse;
import main.api.responses.PostDTO;
import main.api.responses.UserResponse;
import main.model.*;

import java.util.ArrayList;
import java.util.List;

public class PostsMapperImpl {
    public List<PostDTO> postToPostResponse(List<Post> posts) {
        if ( posts == null ) {
            return null;
        }

        List<PostDTO> list = new ArrayList<PostDTO>( posts.size() );
        for ( Post post1 : posts ) {
            list.add( postToPostDTO(post1) );
        }

        return list;
    }

    public PostDTO postToPostDTO(Post post) {
        if ( post == null ) {
            return null;
        }

        PostDTO postDTO = new PostDTO();

        postDTO.setId( post.getId() );
        if ( post.getTime() != null ) {
            postDTO.setTimestamp( (post.getTime().getTime()) / 1000 ) ;
        }
        postDTO.setUser( usersToUserResponse( post.getUser() ) );
        postDTO.setTitle( post.getTitle() );
        postDTO.setAnnounce( post.getTitle() );
        postDTO.setLikeCount( post.getLikeVotes().size() );
        postDTO.setDislikeCount( post.getDislikeVotes().size() );
        postDTO.setViewCount( post.getViewCount() );
        postDTO.setCommentCount( post.getComments().size() );

        return postDTO;
    }

    protected UserResponse usersToUserResponse(User users) {
        if ( users == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId( users.getId() );
        userResponse.setName( users.getName() );

        return userResponse;
    }

    public CertainPostResponse certainPostToPostResponse(Post post)
    {
        if(post == null)
        {
            return null;
        }

        CertainPostResponse certainPostResponse = new CertainPostResponse();

        certainPostResponse.setId(post.getId());
        if( post.getTime() != null )
        {
            certainPostResponse.setTimestamp( (post.getTime().getTime()) / 1000 ) ;
        }
        certainPostResponse.setUser( usersToUserResponse( post.getUser() ) );
        certainPostResponse.setTitle( post.getTitle() );
        certainPostResponse.setText( post.getText() );
        certainPostResponse.setLikeCount( post.getLikeVotes().size() );
        certainPostResponse.setDislikeCount( post.getDislikeVotes().size() );
        certainPostResponse.setViewCount( post.getViewCount() );

        List<PostComment> comments = post.getComments();
        List<CommentResponse> commentsResponse =  new ArrayList<CommentResponse>( comments.size() );
        for ( PostComment comment :  comments ) {
            commentsResponse.add( commentToCommentResponse(comment) );
        }
        certainPostResponse.setComments(commentsResponse);

        List<Tag2Post> tags2Post = post.getTags2Post();
        List<String> tags = new ArrayList( tags2Post.size() );
        for (Tag2Post tag2Post : tags2Post)
        {
            Tag tag = tag2Post.getTagId();
            tags.add(tag.getName());
        }
        certainPostResponse.setTags(tags);

        return certainPostResponse;
    }

    protected CommentResponse commentToCommentResponse(PostComment postComment)
    {
        if(postComment == null)
        {
            return  null;
        }

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(postComment.getId());
        if ( postComment.getTime() != null ) {
            commentResponse.setTimestamp( (postComment.getTime().getTime()) / 1000 ) ;
        }
        commentResponse.setText(postComment.getText());
        commentResponse.setUser( usersToUserResponse(postComment.getUser() ));
        return commentResponse;
    }
}
