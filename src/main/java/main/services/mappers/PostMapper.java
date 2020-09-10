package main.services.mappers;

import main.api.responses.PostResponse;
import main.model.Post;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        uses = {
                UserMapper.class
        }
)
public interface PostMapper {
        PostResponse postToPostResponse(Post post);
        List<PostResponse> postToPostResponse(List<Post> posts);
        //Post requestPostToPost(PostRequest requestPost);
}
