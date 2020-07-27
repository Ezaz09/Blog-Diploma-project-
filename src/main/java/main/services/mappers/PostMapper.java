package main.services.mappers;

import main.api.responses.PostsResponse;
import main.model.Posts;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        uses = {
                UserMapper.class
        }
)
public interface PostMapper {
        PostsResponse postToPostResponse(Posts post);
        List<PostsResponse> postToPostResponse(List<Posts> posts);
        //Post requestPostToPost(PostRequest requestPost);
}
