package main.api.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequestDTO {
    private int parentId;
    private int postId;
    private String text;
}
