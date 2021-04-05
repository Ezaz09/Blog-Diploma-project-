package main.api.mappers;

import main.api.DTO.CommentRequestDTO;
import main.api.requests.CommentRequest;
import main.model.PostComment;
import main.model.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommentMapper {

    public PostComment commentRequestDTOToPostComment(CommentRequestDTO commentRequestDTO,
                                                      User user) {
        if (commentRequestDTO == null) {
            return null;
        }

        PostComment newComment = new PostComment();

        if (commentRequestDTO.getParentId() != 0) {
            newComment.setParentId(commentRequestDTO.getParentId());
        }

        newComment.setPostId(commentRequestDTO.getPostId());
        newComment.setUser(user);
        newComment.setTime(new Date());
        newComment.setText(commentRequestDTO.getText());

        return newComment;
    }

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

    public CommentRequestDTO commentRequestToCommentRequestDTO(CommentRequest commentRequest) {
        if (commentRequest == null) {
            return null;
        }

        CommentRequestDTO commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setParentId(commentRequest.getParentId());
        commentRequestDTO.setPostId(commentRequest.getPostId());
        commentRequestDTO.setText(commentRequest.getText());

        return commentRequestDTO;
    }

}
