package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.DTO.TagDTO;
import main.api.responses.TagsResponse;
import main.model.Tag;
import main.model.Tag2Post;
import main.model.repositories.PostsRepository;
import main.model.repositories.Tag2PostRepository;
import main.model.repositories.TagsRepository;
import main.api.mappers.TagsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TagsService {

    private final TagsRepository tagsRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final PostsRepository postsRepository;

    @Autowired
    public TagsService(TagsRepository tagsRepository,
                       Tag2PostRepository tag2PostRepository,
                       PostsRepository postsRepository) {
        this.tagsRepository = tagsRepository;
        this.tag2PostRepository = tag2PostRepository;
        this.postsRepository = postsRepository;
    }

    public ResponseEntity<TagsResponse> getTags() {
        List<Tag> allTags = tagsRepository.findAll();
        List<TagDTO> listOfTags = new TagsMapper(postsRepository).tagsToTagsResponse(allTags);
        TagsResponse tagsResponse = TagsResponse.builder()
                .tags(listOfTags).build();
        return new ResponseEntity<>(tagsResponse, HttpStatus.OK);
    }

    public void setTagsForNewPost(List<String> tagsRequest,
                                  int postId) {
        for (String tag : tagsRequest) {
            Tag finedTag = tagsRepository.findByTagName(tag);

            Tag2Post tag2Post = new Tag2Post();
            tag2Post.setPostId(postId);

            if (finedTag == null) {
                Tag newTag = new Tag();
                newTag.setName(tag);
                tagsRepository.save(newTag);

                tag2Post.setTag(newTag);
            } else {
                tag2Post.setTag(finedTag);
            }

            tag2PostRepository.save(tag2Post);
        }
    }

    public void setTagsForEditingPost(List<String> tagsRequest,
                                      List<Tag2Post> tag2PostFromPost,
                                      int postId) {
        for (Tag2Post tag2Post : tag2PostFromPost) {
            tag2PostRepository.delete(tag2Post);
        }

        for (String nameOfTag : tagsRequest) {
            Tag tag = tagsRepository.findByTagName(nameOfTag);
            if (tag == null) {
                Tag newTag = new Tag();
                newTag.setName(nameOfTag);
                tagsRepository.save(newTag);
                tag = newTag;
            }

            Tag2Post newTag2Post = new Tag2Post();
            newTag2Post.setPostId(postId);
            newTag2Post.setTag(tag);
            tag2PostRepository.save(newTag2Post);
        }
    }
}
