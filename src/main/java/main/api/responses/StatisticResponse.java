package main.api.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticResponse {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private Long firstPublication;
}
