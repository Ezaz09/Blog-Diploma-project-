package main.services;

import main.api.responses.LikeDislikeResponse;
import main.api.responses.post_responses.EditPostByModeratorResponse;
import main.api.responses.post_responses.EditPostResponse;
import main.api.responses.post_responses.NewPostResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ResponseService {

    public EditPostByModeratorResponse createNewEditPostByModeratorResponse() {
        EditPostByModeratorResponse editPostByModeratorResponse = new EditPostByModeratorResponse();
        editPostByModeratorResponse.setResult(true);
        editPostByModeratorResponse.setErrors(new HashMap<>());

        return editPostByModeratorResponse;
    }

    public NewPostResponse createNewPostResponse() {
        return new NewPostResponse();
    }

    public EditPostResponse createNewEditPostResponse() {
        EditPostResponse editPostResponse = new EditPostResponse();
        editPostResponse.setResult(true);
        editPostResponse.setErrors(new HashMap<>());

        return editPostResponse;
    }

    public LikeDislikeResponse createNewLikeDislikeResponse() {
        return new LikeDislikeResponse();
    }
}
