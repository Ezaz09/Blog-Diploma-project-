package main.api.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Tag;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditPostRequest {
    private Long timestamp;
    private int active;
    private String title;
    private List<String> tags;
    private String text;

}
