package main.api.mappers;

import main.api.DTO.PostRequestDTO;
import main.api.requests.EditPostRequest;
import main.api.requests.PostRequest;
import main.api.responses.CommentResponse;
import main.api.responses.post_responses.CertainPostResponse;
import main.api.responses.post_responses.CountOfPostsPerYearResponse;
import main.api.DTO.PostDTO;
import main.api.responses.user_response.UserResponse;
import main.model.*;
import main.model.enums.ModerationStatus;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class PostsMapper {

    public List<PostDTO> postToPostResponse(@NotNull List<Post> posts) {
        List<PostDTO> list = new ArrayList<>(posts.size());

        if (posts.size() == 0) {
            return list;
        }

        for (Post post1 : posts) {
            list.add(postToPostDTO(post1));
        }

        return list;
    }

    public PostDTO postToPostDTO(Post post) {
        if (post == null) {
            return null;
        }

        PostDTO postDTO = new PostDTO();

        postDTO.setId(post.getId());
        if (post.getTime() != null) {
            postDTO.setTimestamp((post.getTime().getTime()) / 1000);
        }
        postDTO.setUser(usersToUserResponse(post.getUser()));
        postDTO.setTitle(post.getTitle());

        String announce = post.getText().replaceAll("<[^>]*>", "");
        postDTO.setAnnounce(announce);

        postDTO.setLikeCount(post.getLikeVotes().size());
        postDTO.setDislikeCount(post.getDislikeVotes().size());
        postDTO.setViewCount(post.getViewCount());
        postDTO.setCommentCount(post.getComments().size());

        return postDTO;
    }

    protected UserResponse usersToUserResponse(User users) {
        if (users == null) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId(users.getId());
        userResponse.setName(users.getName());

        return userResponse;
    }

    public CertainPostResponse certainPostToPostResponse(Post post) {
        if (post == null) {
            return null;
        }

        CertainPostResponse certainPostResponse = new CertainPostResponse();

        certainPostResponse.setId(post.getId());
        if (post.getTime() != null) {
            certainPostResponse.setTimestamp((post.getTime().getTime()) / 1000);
        }
        certainPostResponse.setUser(usersToUserResponse(post.getUser()));
        certainPostResponse.setTitle(post.getTitle());
        certainPostResponse.setText(post.getText());
        certainPostResponse.setLikeCount(post.getLikeVotes().size());
        certainPostResponse.setDislikeCount(post.getDislikeVotes().size());
        certainPostResponse.setViewCount(post.getViewCount());

        List<PostComment> comments = post.getComments();
        List<CommentResponse> commentsResponse = new ArrayList<CommentResponse>(comments.size());
        for (PostComment comment : comments) {
            commentsResponse.add(commentToCommentResponse(comment));
        }
        certainPostResponse.setComments(commentsResponse);

        List<Tag2Post> tags2Post = post.getTags2Post();
        List<String> tags = new ArrayList(tags2Post.size());
        for (Tag2Post tag2Post : tags2Post) {
            Tag tag = tag2Post.getTag();
            tags.add(tag.getName());
        }
        certainPostResponse.setTags(tags);

        return certainPostResponse;
    }

    protected CommentResponse commentToCommentResponse(PostComment postComment) {
        if (postComment == null) {
            return null;
        }

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(postComment.getId());
        if (postComment.getTime() != null) {
            commentResponse.setTimestamp((postComment.getTime().getTime()) / 1000);
        }
        commentResponse.setText(postComment.getText());
        commentResponse.setUser(usersToUserResponse(postComment.getUser()));
        return commentResponse;
    }

    public Post newPostDTOToPost(PostRequestDTO postRequestDTO,
                                 User user,
                                 User moderator,
                                 boolean needModeration) {
        if (postRequestDTO == null) {
            return null;
        }

        Post post = new Post();

        post.setIsActive(postRequestDTO.getActive());

        if(user.getIsModerator() == 0 && needModeration) {
            post.setModerationStatus(ModerationStatus.NEW);
        } else {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        }

        post.setModerator(moderator);
        post.setUser(user);
        post.setTime(new Date(postRequestDTO.getTimestamp() * 1000));
        post.setTitle(postRequestDTO.getTitle());
        post.setText(postRequestDTO.getText());
        post.setViewCount(0);

        return post;
    }

    public PostRequestDTO newPostRequestToPostRequestDTO(PostRequest postRequest) {
        if (postRequest == null) {
            return null;
        }

        return postRequestToPostRequestDTO(postRequest);
    }

    public PostRequestDTO editPostRequestToPostRequestDTO(EditPostRequest editPostRequest) {
        if (editPostRequest == null) {
            return null;
        }

        return postRequestToPostRequestDTO(editPostRequest);
    }

    private PostRequestDTO postRequestToPostRequestDTO(PostRequest postRequest) {
        PostRequestDTO postRequestDTO = new PostRequestDTO();

        postRequestDTO.setTimestamp(postRequest.getTimestamp());
        postRequestDTO.setActive(postRequest.getActive());
        postRequestDTO.setTitle(postRequest.getTitle());
        postRequestDTO.setTags(postRequest.getTags());
        postRequestDTO.setText(postRequest.getText());

        return postRequestDTO;
    }

    public CountOfPostsPerYearResponse postToCountOfPostsPerYear(List<Post> posts, int year) {
        if (posts == null) {
            return null;
        }

        CountOfPostsPerYearResponse countOfPostsPerYearResponse = new CountOfPostsPerYearResponse();
        List<Integer> years = new ArrayList<>();
        years.add(year);

        HashMap<String, Integer> countOfPostsPerDay = new HashMap<>(posts.size());

        countOfPostsPerYearResponse.setYears(years);
        countOfPostsPerYearResponse.setPosts(countOfPostsPerDay);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Post post : posts) {
            String dateForMap = simpleDateFormat.format(post.getTime());
            if (countOfPostsPerDay.containsKey(dateForMap)) {
                int countOfPostPerCertainDay = countOfPostsPerDay.get(dateForMap);
                countOfPostPerCertainDay += 1;
                countOfPostsPerDay.put(dateForMap, countOfPostPerCertainDay);
            } else {
                countOfPostsPerDay.put(dateForMap, 1);
            }

        }

        return countOfPostsPerYearResponse;
    }
}
