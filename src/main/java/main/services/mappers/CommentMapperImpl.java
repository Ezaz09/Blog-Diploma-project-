package main.services.mappers;

import main.api.requests.CommentRequest;
import main.model.PostComment;
import main.model.User;

import java.util.Date;

public class CommentMapperImpl {

    public PostComment commentRequestToPostComment(CommentRequest commentRequest,
                                                   User user) {
        if (commentRequest == null) {
            return null;
        }

        PostComment newComment = new PostComment();

        if (commentRequest.getParentId() != 0) {
            newComment.setParentId(commentRequest.getParentId());
        }

        newComment.setPostId(commentRequest.getPostId());
        newComment.setUser(user);
        newComment.setTime(new Date());
        newComment.setText(commentRequest.getText());

        return newComment;
    }

}
