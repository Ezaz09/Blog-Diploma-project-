package main.api.responses.post_responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountOfPostsPerYearResponse {
    private List<Integer> years;
    private HashMap<String, Integer> posts;
}
