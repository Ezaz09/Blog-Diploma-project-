package main.api.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostRequestDTO {
    private Long timestamp;
    private int active;
    private String title;
    private List<String> tags;
    private String text;
}
