package main.controller;

import main.api.mappers.PostsMapper;
import main.api.requests.EditPostRequest;
import main.api.requests.LikeDislikeRequest;
import main.api.requests.PostRequest;
import main.api.responses.LikeDislikeResponse;
import main.api.responses.post_responses.*;
import main.model.Post;
import main.services.PostsService;
import main.services.ResponseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostsService postsService;

    private final PostsMapper postsMapper;

    private final ResponseService responseService;


    public ApiPostController(PostsService postsService,
                             PostsMapper postsMapper,
                             ResponseService responseService) {
        this.postsService = postsService;
        this.postsMapper = postsMapper;
        this.responseService = responseService;
    }

    @GetMapping(path = "")
    public ResponseEntity<PostResponse> listOfPosts(@RequestParam(defaultValue = "0", required = false) int offset,
                                                    @RequestParam(defaultValue = "20", required = false) int limit,
                                                    @RequestParam(defaultValue = "recent", required = false) String mode) {
        List<Post> posts = postsService.getPosts(offset, limit, mode);

        PostResponse postResponse = transferListOfPostsToListOfPostDTO(posts);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @PostMapping(path = "")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<NewPostResponse> addNewPost(@RequestBody PostRequest postRequest,
                                                      Principal principal) {
        /**
         * Форматы ответа
         * В случае успеха
         * {
         * 	"result": true
         * }
         * В случае ошибок
         * {
         *   "result": false,
         *   "errors": {
         *     "title": "Заголовок не установлен",
         *     "text": "Текст публикации слишком короткий"
         *   }
         * }
         */
        HashMap<String, String> mapOfErrors = checkParamsOfPost(postRequest.getTitle(),
                                                                postRequest.getText(),
                                                                principal);
        NewPostResponse newPostResponse = responseService.createNewPostResponse();

        if (!mapOfErrors.isEmpty()) {
            newPostResponse.setResult(false);
            newPostResponse.setErrors(mapOfErrors);
            return new ResponseEntity<>(newPostResponse, HttpStatus.OK);
        }

        boolean isNewPostCreated = postsService.addNewPost(postRequest, principal.getName());

        if(isNewPostCreated) {
            newPostResponse.setResult(true);
        } else {
            newPostResponse.setResult(false);
            mapOfErrors.put("post", "Произошла ошибка при создании поста");
            newPostResponse.setErrors(mapOfErrors);
        }
        return new ResponseEntity<>(newPostResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<PostResponse> getPostsByQuery(@RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "20") int limit,
                                                        @RequestParam String query) {

        List<Post> postsByQuery = postsService.findPostsByQuery(offset, limit, query);

        PostResponse postResponse = transferListOfPostsToListOfPostDTO(postsByQuery);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/byDate")
    public ResponseEntity<PostResponse> getPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                       @RequestParam(defaultValue = "20") int limit,
                                                       @RequestParam String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startOfDay;
        try {
            startOfDay = simpleDateFormat.parse(date);
        } catch (ParseException ex) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<Post> allPosts = postsService.findPostsByDate(offset, limit, startOfDay);

        PostResponse postResponse = transferListOfPostsToListOfPostDTO(allPosts);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/byTag")
    public ResponseEntity<PostResponse> getPostsByTag(@RequestParam(defaultValue = "0") int offset,
                                                      @RequestParam(defaultValue = "20") int limit,
                                                      @RequestParam String tag) {
        List<Post> allPosts = postsService.findPostsByTag(offset, limit, tag);

        PostResponse postResponse = transferListOfPostsToListOfPostDTO(allPosts);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CertainPostResponse> getPost(@PathVariable int id,
                                                       Principal principal) {
        String userEmail;

        if(principal != null) {
            userEmail = principal.getName();
        } else {
            userEmail = null;
        }

        Post postById = postsService.findPostById(id, userEmail);

        if (postById == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        CertainPostResponse certainPostResponse;
        certainPostResponse = postsMapper.certainPostToPostResponse(postById);

        return new ResponseEntity<>(certainPostResponse, HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<EditPostResponse> editPost(@PathVariable int id,
                                                     @RequestBody EditPostRequest editPostRequest,
                                                     Principal principal) {
        /**
         * Форматы ответа
         * В случае успеха
         * {
         * 	"result": true
         * }
         * В случае ошибок
         * {
         *   "result": false,
         *   "errors": {
         *     "title": "Заголовок слишком короткий",
         *     "text": "Текст публикации слишком короткий"
         *   }
         * }
         */

        HashMap<String, String> mapOfErrors = checkParamsOfPost(editPostRequest.getTitle(),
                                                                editPostRequest.getText(),
                                                                principal);
        EditPostResponse editPostResponse = responseService.createNewEditPostResponse();

        if (!mapOfErrors.isEmpty()) {
            editPostResponse.setResult(false);
            editPostResponse.setErrors(mapOfErrors);
            return new ResponseEntity<>(editPostResponse, HttpStatus.OK);
        }

        mapOfErrors = postsService.editPost(id, editPostRequest, principal.getName());

        editPostResponse.setResult(mapOfErrors.isEmpty());
        editPostResponse.setErrors(mapOfErrors);

        return new ResponseEntity<>(editPostResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/my")
    public ResponseEntity<PostResponse> getUserPosts(@RequestParam(defaultValue = "0") int offset,
                                                     @RequestParam(defaultValue = "20") int limit,
                                                     @RequestParam(defaultValue = "inactive") String status,
                                                     Principal principal) {
        if (principal == null) {
            PostResponse postResponse = PostResponse.builder()
                    .count(0)
                    .posts(Collections.emptyList()).build();
            return new ResponseEntity<>(postResponse, HttpStatus.OK);
        }

        List<Post> allPosts = postsService.findUserPosts(offset, limit, status, principal.getName());

        PostResponse postResponse = transferListOfPostsToListOfPostDTO(allPosts);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }


    @PostMapping(path = "/like")
    public ResponseEntity<LikeDislikeResponse> addNewLike(@RequestBody LikeDislikeRequest likeDislikeRequest,
                                                          Principal principal) {
        /**
         * Формат ответа в случае если лайк прошел
         * {
         *   "result": true / false в случае ошибки
         * }
         */
        if (principal == null) {
            return getNewLikeDislikeResponse(false);
        }

        boolean isNewLikeSaved = postsService.addNewLikeDislike(likeDislikeRequest, principal.getName(), true);

        return getNewLikeDislikeResponse(isNewLikeSaved);
    }

    @PostMapping(path = "/dislike")
    public ResponseEntity<LikeDislikeResponse> addNewDislike(@RequestBody LikeDislikeRequest likeDislikeRequest,
                                                             Principal principal) {
        /**
         * Формат ответа в случае если лайк прошел
         * {
         *   "result": true / false в случае ошибки
         * }
         */
        if (principal == null) {
            return getNewLikeDislikeResponse(false);
        }

        boolean isNewDislikeSaved = postsService.addNewLikeDislike(likeDislikeRequest, principal.getName(), false);

        return getNewLikeDislikeResponse(isNewDislikeSaved);
    }

    private HashMap<String, String> checkParamsOfPost(String editPostRequestTitle,
                                                      String editPostRequestText,
                                                      Principal principal) {
        HashMap<String, String> mapOfErrors = new HashMap<>();

        if(principal == null) {
            mapOfErrors.put("auth", "Пользователь не авторизован");
        }

        if (editPostRequestTitle.length() < 3 ||
                editPostRequestText.length() < 50) {

            if (editPostRequestTitle.length() < 3) {
                mapOfErrors.put("title", "Заголовок слишком короткий");
            }

            if (editPostRequestText.length() < 50) {
                mapOfErrors.put("text", "Текст публикации слишком короткий");
            }
        }

        return mapOfErrors;
    }

    private PostResponse transferListOfPostsToListOfPostDTO(List<Post> posts) {
        List<PostDTO> listOfPosts = postsMapper.postToPostResponse(posts);
        return PostResponse.builder()
                .count(listOfPosts.size())
                .posts(listOfPosts).build();
    }

    private ResponseEntity<LikeDislikeResponse> getNewLikeDislikeResponse(boolean result) {
        LikeDislikeResponse likeDislikeResponse = responseService.createNewLikeDislikeResponse();
        likeDislikeResponse.setResult(result);
        return new ResponseEntity<>(likeDislikeResponse, HttpStatus.OK);
    }
}
