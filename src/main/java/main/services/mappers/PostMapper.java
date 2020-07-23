package main.services.mappers;

import main.api.responses.PostsResponseHQL;
import main.model.Posts;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        uses = {
                UserMapper.class
        }
)
public interface PostMapper {
        PostsResponseHQL postToPostResponse(Posts post);
        List<PostsResponseHQL> postToPostResponse(List<Posts> posts);
        //Post requestPostToPost(PostRequest requestPost);
}
