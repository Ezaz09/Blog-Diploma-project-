package main.api.responses.post_responses;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostResponse {
    private int count;
    private List<PostDTO> posts;
}
