package main.api.responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostsResponse{

    private int count;

    private List<PostDTO> posts;
}
