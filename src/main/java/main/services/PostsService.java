package main.services;

import main.api.DTO.PostRequestDTO;
import main.api.mappers.PostsMapper;
import main.api.DTO.UserAndPostDTO;
import main.model.GlobalSetting;
import main.model.Post;
import main.model.PostVote;
import main.model.User;
import main.model.enums.ModerationStatus;
import main.model.repositories.PostVotesRepository;
import main.model.repositories.PostsRepository;
import main.model.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class PostsService {

    private final PostsRepository postsRepository;

    private final UsersRepository usersRepository;

    private final PostVotesRepository postVotesRepository;

    private final TagsService tagsService;

    private final GlobalSettingsService globalSettingsService;


    @Autowired
    public PostsService(PostsRepository postsRepository,
                        UsersRepository usersRepository,
                        PostVotesRepository postVotesRepository,
                        TagsService tagsService,
                        GlobalSettingsService globalSettingsService) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.postVotesRepository = postVotesRepository;
        this.tagsService = tagsService;
        this.globalSettingsService = globalSettingsService;
    }

    public List<Post> getPosts(int offset,
                               int limit,
                               String mode) {
        /**
         * Функция возвращает список всех постов, которые сохранены в блоге
         * Параметры функции
         * offset - количество постов в начале списка, которое не попадет в результат запроса
         * limit - ограничение на количество постов в результате запроса
         * mode - режим сортировки результата запроса. Если будет указан не существующий режим - функция вернет пустой ArrayList.
         */

        List<Post> allPosts = new ArrayList<>();
        switch (mode) {
            case "best":
                allPosts = postsRepository.getPostsSortByLikeVotes(PageRequest.of((offset / limit), limit));
                break;
            case "popular":
                allPosts = postsRepository.getPostsSortByComments(PageRequest.of((offset / limit), limit));
                break;
            case "early":
                allPosts = postsRepository.getPostsSortByDateAsc(PageRequest.of((offset / limit), limit));
                break;
            case "recent":
                allPosts = postsRepository.getPostsSortByDateDesc(PageRequest.of((offset / limit), limit));
        }

        return allPosts;
    }

    public List<Post> findPostsByQuery(int offset,
                                       int limit,
                                       String query) {
        /**
         * Функция возвращает список постов, заголовок которых
         * содержит/равно значению параметра "query"
         * Параметры функции
         * offset - количество постов в начале списка, которое не попадет в результат запроса
         * limit - ограничение на количество постов в результате запроса
         * query - параметр для поиска постов
         */

        Pageable pageable = PageRequest.of(offset, limit);
        return postsRepository.getPostsByQuery(pageable, query);
    }

    public List<Post> findPostsByDate(int offset,
                                      int limit,
                                      Date startOfDay) {
        /**
         * Функция находит посты, которые были созданы в пределах переданной в параметрах даты
         * Параметры функции
         * offset - количество постов в начале списка, которое не попадет в результат запроса
         * limit - ограничение на количество постов в результате запроса
         * startOfDay - дата, на которую требуется получить посты
         */

        Pageable pageable = PageRequest.of(offset, limit);

        LocalDateTime localDateTime = dateToLocalDateTime(startOfDay);
        LocalDateTime endOfDayLocalDate = localDateTime.with(LocalTime.MAX);
        Date endOfDay = localDateTimeToDate(endOfDayLocalDate);

        List<Post> allPosts = postsRepository.getPostsByDate(pageable, startOfDay, endOfDay);
        return allPosts;
    }

    public List<Post> findPostsByTag(int offset,
                                     int limit,
                                     String tag) {
        /**
         * Функция производит поиск постов в базе данных,
         * которые имеют тэг, который был передан в параметре tag
         * Параметры функции
         * offset - количество постов в начале списка, которое не попадет в результат запроса
         * limit - ограничение на количество постов в результате запроса
         * tag - тэг, по которому производится поиск постов
         */

        Pageable pageable = PageRequest.of(offset, limit);
        return postsRepository.getPostsByTag(pageable, tag);
    }

    public Post findPostById(int id,
                             String userEmail) {
        /**
         * Функция получает из БД пост по переданному id
         * А так же пользователя, если он авторизован
         * Увеличивает значение количества просмотров поста (если все условия были пройдены)
         * И возвращает полученный пост
         * Параметры функции
         * id - id нужного поста
         * userEmail - email пользователя
         */

        User user;
        Post post;
        UserAndPostDTO userAndPost = getUserAndPostAndCheckRelationshipOfUserAndPost(userEmail, id);

        post = userAndPost.getPost();
        user = userAndPost.getUser();

        if (post == null) {
            return null;
        }

        setViewCountForPost(post, user);

        return post;
    }

    public List<Post> findUserPosts(int offset,
                                    int limit,
                                    String mode,
                                    String userEmail) {
        /**
         * Функция находит посты авторизованного пользователя
         * определенного типа, который был передан в параметре mode
         * Параметры функции
         * offset - количество постов в начале списка, которое не попадет в результат запроса
         * limit - ограничение на количество постов в результате запроса
         * mode - тип постов пользователя
         * userEmail - email пользователя
         */

        Pageable pageable = PageRequest.of(offset, limit);
        User user = usersRepository.findByEmail(userEmail);

        List<Post> allPosts = new ArrayList<>();
        switch (mode) {
            case "inactive":
                allPosts = postsRepository.getInactiveUserPosts(pageable, user);
                break;
            case "pending":
                allPosts = postsRepository.getPendingUserPosts(pageable, user);
                break;
            case "declined":
                allPosts = postsRepository.getDeclinedUserPosts(pageable, user);
                break;
            case "published":
                allPosts = postsRepository.getPublishedUserPosts(pageable, user);
                break;
        }

        return allPosts;
    }

    public List<Post> findModeratorPosts(int offset,
                                         int limit,
                                         String status,
                                         String moderatorEmail) {
        /**
         * Функция находит посты авторизованного модератора
         * определенного статуса, который был передан в параметре status
         * Параметры функции
         * offset - количество постов в начале списка, которое не попадет в результат запроса
         * limit - ограничение на количество постов в результате запроса
         * status - статус постов пользователей
         * moderatorEmail - email модератора
         */

        List<Post> moderatorPosts = new ArrayList<>();
        ModerationStatus moderationStatus = null;
        switch (status) {
            case "new":
                moderationStatus = ModerationStatus.NEW;
                break;
            case "accepted":
                moderationStatus = ModerationStatus.ACCEPTED;
                break;
            case "declined":
                moderationStatus = ModerationStatus.DECLINED;
                break;
        }

        if (moderationStatus == null) {
            return moderatorPosts;
        }

        Pageable pageable = PageRequest.of(offset, limit);
        User moderator = usersRepository.findByEmail(moderatorEmail);
        moderatorPosts = postsRepository.getModeratorPosts(pageable, moderator, moderationStatus);

        return moderatorPosts;
    }

    public boolean addNewPost(PostRequestDTO postRequestDTO,
                              String userEmail) {
        /**
         * Функция создает новый пост на основе данных, переданных в postRequest
         * в случае если пост был создан и сохранен, функция возвращает true
         * если произошла ошибка в процессе мэппинга postRequest, функция возвращает false
         * Параметры функции
         * postRequestDTO - пост из реквеста для добавления в базу данных
         * userEmail - email пользователя
         */

        User user = usersRepository.findByEmail(userEmail);
        User moderator = getRandomModeratorForPostModeration();

        boolean needModeration = isPremoderationForPostsNeeded();

        Post newPost = new PostsMapper().newPostDTOToPost(postRequestDTO,
                user,
                moderator,
                needModeration);

        if (newPost == null) {
            return false;
        }

        postsRepository.save(newPost);
        tagsService.setTagsForNewPost(postRequestDTO.getTags(),
                newPost.getId());

        return true;
    }

    public HashMap<String, String> editPost(int id,
                                            PostRequestDTO postRequestDTO,
                                            String userEmail) {
        /**
         * Функция находит по переданному id пост в базе данных
         * и в случае если пост был найден,
         * изменяет параметры поста на те что были переданны в параметре postRequestDTO
         * Возвращает HashMap, который содержит в себе список ошибок, если такие возникли
         * в процессе выполнения кода.
         * Параметры функции
         * postRequestDTO - пост из реквеста для изменения
         * userEmail - email пользователя
         */

        HashMap<String, String> mapOfErrors = new HashMap<>();

        User user;
        Post post;
        UserAndPostDTO userAndPost = getUserAndPostAndCheckRelationshipOfUserAndPost(userEmail, id);

        post = userAndPost.getPost();
        user = userAndPost.getUser();

        if (post == null) {
            mapOfErrors.put("text", "Запрашиваемый пост не был найден!");

            return mapOfErrors;
        }

        boolean changeStatus;

        assert user != null;
        changeStatus = user.getIsModerator() != 1;

        post = editParamsOfPost(postRequestDTO,
                post,
                changeStatus);

        postsRepository.save(post);
        tagsService.setTagsForEditingPost(postRequestDTO.getTags(),
                post.getTags2Post(),
                post.getId());

        return mapOfErrors;
    }


    public HashMap<String, String>  editPostByModerator(int postId,
                                                        String decision,
                                                        User moderator) {
        /**
         * Функция получает запрос на изменение решения по посту и пользователя
         * проверяет, является ли переданный пользователь модератором
         * и меняет статус модерации у поста
         * Параметры функции
         * postId - id поста, по которому было изменено решение модератора,
         * decision - решение, которое было принято модератором,
         * moderator - модератор
         */
        HashMap<String, String> mapOfErrors = new HashMap<>();

        if (postId == 0 ||
                decision.equals("")) {
            mapOfErrors.put("params", "Переданы неверные параметры!");
        }

        if (moderator.getIsModerator() == 0) {
            mapOfErrors.put("auth", "Пользователь не являеться модератором!");

        }

        if (!mapOfErrors.isEmpty()) {
            return mapOfErrors;
        }

        Post post = postsRepository.getCertainPostForModerators(postId);

        if (post == null) {
            mapOfErrors.put("post", "Запрошенный пост не был найден!");
            return mapOfErrors;
        }

        if (decision.equals("accept")) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else if (decision.equals("decline")) {
            post.setModerationStatus(ModerationStatus.DECLINED);
        } else {
            mapOfErrors.put("decision", "Переданное решение по посту не распознано!");
            return mapOfErrors;
        }

        post.setModerator(moderator);
        postsRepository.save(post);

        return mapOfErrors;
    }

    public List<Post> getCountOfPostsPerYear(int year) {
        return postsRepository.getCountOfPostsPerYear(year);
    }

    public boolean addNewLikeDislike(int postId,
                                     String userEmail,
                                     boolean likeDislike) {
        /**
         * Функция создает новый лайк/дизлайк, взависимости от параметра likeDislike
         * для поста, id которого передается в параметре postId, или заменяет существующий
         * лайк на дизлайк в базе данных и наоборот
         * Параметры функции
         * postId - id поста для которого нужно проставить лайк/дизлайк
         * userEmail - email пользователя
         * likeDislike - лайк(true) или дизлайк(false)
         */

        User user = usersRepository.findByEmail(userEmail);

        if(user == null) {
            return false;
        }

        Post certainPost = postsRepository.getCertainPost(postId);

        if (certainPost == null) {
            return false;
        }

        /**
         * произведем поиск лайка/дизлайка для переданного поста пользователем
         * если поиск не дал результатов - создаем новый лайк/дизлайк
         * но если, к примеру, был найден лайк, а пользователь теперь ставит дизлайк, то меняем лайк на дизлайк и так же для обратного случая
         * если был найден лайк и пользователь ставит лайк, то ничего не делаем
         */

        PostVote postVoteByUserId = postVotesRepository.findPostVoteByUserId(user.getId(), certainPost.getId());

        if (postVoteByUserId == null) {
            PostVote postVote = setNewLikeDislike(likeDislike,
                                                  certainPost.getId(),
                                                  user.getId());
            postVotesRepository.save(postVote);
        } else if (postVoteByUserId.getValue() == 1) {
            if(!likeDislike) {
                changeParamOfPostVote(postVoteByUserId, -1);
                return true;
            }
           return false;
        } else if (postVoteByUserId.getValue() == -1) {
            if(likeDislike) {
                changeParamOfPostVote(postVoteByUserId, 1);
                return true;
            }
            return false;
        }
        return true;
    }

    private void changeParamOfPostVote(PostVote postVoteByUserId, int value) {
        postVoteByUserId.setValue(value);
        postVotesRepository.save(postVoteByUserId);
    }

    private PostVote setNewLikeDislike(boolean isLike,
                                         int postId,
                                         int userId) {
        PostVote postVote = new PostVote();
        postVote.setUserId(userId);
        postVote.setPostId(postId);
        postVote.setTime(new Date());

        if (isLike) {
            postVote.setValue(1);
        } else {
            postVote.setValue(-1);
        }

        return postVote;
    }

    private void setViewCountForPost(Post post,
                                     User user) {
        /**
          Проверим переданного пользователя
          если пользователь не является модератором и не являеться автором поста,
          то увеличим количество просмотров поста

          если пользователь не авторизован (null), то тоже увеличим количество просмотров
         */

        if(user != null && (user.getIsModerator() == 1 || user.getId() == post.getUser().getId())) {
            return;
        }

        post.setViewCount(post.getViewCount() + 1);
        postsRepository.save(post);
    }

    private UserAndPostDTO getUserAndPostAndCheckRelationshipOfUserAndPost(String userEmail,
                                                                           int id) {
        UserAndPostDTO userAndPost = new UserAndPostDTO();

        /**
          Получим пользователя для проверки поста
         */

        User user = null;
        if (userEmail != null) {
            user = usersRepository.findByEmail(userEmail);
        }

        /**
          В случае если пользователь был найден и не является модератором
          проверим, являеться ли полученный пользователь
          автором поста, для случаев когда автор поста захочет
          посмотреть/внести изменения в свой пост
          в момент когда пост еще не одобрен/отклонен модератором

          Если пользователь не был найден - значит запрос поста приходит с главной страницы
          где посты могут просматривать не авторизованные пользователи
         */

        Post post;
        if (user != null) {
            post = postsRepository.getCertainPostForModerators(id);
            if (user.getIsModerator() != 1 &&
                    post.getModerationStatus() != ModerationStatus.ACCEPTED &&
                    post.getUser() != user) {
                post = null;
            }
        } else {
            post = postsRepository.getCertainPost(id);
        }

        userAndPost.setPost(post);
        userAndPost.setUser(user);

        return userAndPost;
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private User getRandomModeratorForPostModeration() {
        /**
         * Функция находит всех модераторов в базе данных
         * и возвращает одного случайно выбранного модератора
         */

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> moderatorList = usersRepository.findModerator(pageRequest);

        int max = moderatorList.size() - 1;
        int rand = (int) (Math.random() * ++max);
        return moderatorList.get(rand);
    }

    private boolean isPremoderationForPostsNeeded(){
        /**
         * Функция определяет, требуется ли премодерация новых постов
         * перед публикаций на главной странице блога
         */

        GlobalSetting postPremoderation = globalSettingsService.getGlobalSettingForAPIByName("POST_PREMODERATION");
        boolean needModeration = true;

        if(postPremoderation != null)
        {
            if(postPremoderation.getValue().equals("false")) {
                needModeration = false;
            }
        }

        return needModeration;
    }

    public Post editParamsOfPost(PostRequestDTO postRequestDTO,
                                 Post post,
                                 boolean changeStatus) {
        if (post == null) {
            return null;
        }

        if (changeStatus) {
            post.setModerationStatus(ModerationStatus.NEW);
        }

        post.setTime(new Date(postRequestDTO.getTimestamp() * 1000));
        post.setTitle(postRequestDTO.getTitle());
        post.setText(postRequestDTO.getText());

        return post;
    }
}

