package main.services.mappers;

import main.api.responses.StatisticResponse;
import main.model.Post;

import java.util.List;

public class StatisticMapperImpl {
    public StatisticResponse postsToStatisticResponse(List<Post> posts) {
        if ( posts == null ) {
            return null;
        }

        int postsCount = posts.size();
        int likesCount = 0;
        int dislikesCount = 0 ;
        int viewsCount = 0;
        Long firstPublication = null;
        for( Post post : posts ) {

            if(firstPublication == null)
            {
                firstPublication = (post.getTime().getTime()) / 1000;
            }
            else {
                long dateOfPublication = (post.getTime().getTime()) / 1000;
                if( firstPublication > dateOfPublication )
                {
                    firstPublication = dateOfPublication;
                }
            }

            likesCount = likesCount + post.getLikeVotes().size();
            dislikesCount = dislikesCount + post.getDislikeVotes().size();
            viewsCount = viewsCount + post.getViewCount();

        }

        StatisticResponse statisticResponse = StatisticResponse.builder()
                .postsCount(postsCount)
                .likesCount(likesCount)
                .dislikesCount(dislikesCount)
                .viewsCount(viewsCount)
                .firstPublication(firstPublication).build();

        return statisticResponse;
    }
}
