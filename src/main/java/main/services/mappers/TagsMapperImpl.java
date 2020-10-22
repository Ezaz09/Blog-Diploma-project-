package main.services.mappers;

import main.api.responses.TagDTO;
import main.model.Post;
import main.model.Tag;
import main.model.repositories.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagsMapperImpl {
    private final PostsRepository postsRepository;
    private HashMap<String, Double> tableOfWeightsOfTags;

    @Autowired
    public TagsMapperImpl(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    public List<TagDTO> tagsToTagsResponse(List<Tag> tags) {
        if (tags == null) {
            return null;
        }

        calculateWeightOfTag(tags);

        List<TagDTO> list = new ArrayList<>(tags.size());
        for (Tag tag1 : tags) {
            list.add(tagToTagDTO(tag1));
        }

        return list;
    }

    public TagDTO tagToTagDTO(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagDTO tagDTO = new TagDTO();
        tagDTO.setName(tag.getName());
        if (tableOfWeightsOfTags.isEmpty()) {
            tagDTO.setWeight(0);
        } else {
            tagDTO.setWeight(tableOfWeightsOfTags.get(tag.getName()));
        }

        return tagDTO;
    }

    protected void calculateWeightOfTag(List<Tag> tags) {
        List<Post> allPosts = postsRepository.findAll();
        tableOfWeightsOfTags = new HashMap<>();
        int countOfPosts = allPosts.size();

        if (countOfPosts == 0) {
            return;
        }

        for (Tag tag : tags) {
            Pageable pageable = PageRequest.of(0, countOfPosts);
            List<Post> postsByTag = postsRepository.getPostsByTag(pageable, tag.getName());
            int countOfPostsWithTag = postsByTag.size();

            if (countOfPostsWithTag == 0) {
                tableOfWeightsOfTags.put(tag.getName(), 0.0);
            } else {
                double weight = (double) countOfPostsWithTag / countOfPosts;
                double scale = Math.pow(10, 2);
                double result = Math.ceil(weight * scale) / scale;
                tableOfWeightsOfTags.put(tag.getName(), result);
            }
        }
    }
}
