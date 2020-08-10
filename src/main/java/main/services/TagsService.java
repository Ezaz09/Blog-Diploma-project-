package main.services;

import lombok.extern.slf4j.Slf4j;
import main.api.responses.TagDTO;
import main.api.responses.TagsResponse;
import main.model.Tags;
import main.model.repositories.TagsRepository;
import main.services.mappers.TagsMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TagsService {

    private final TagsRepository tagsRepository;

    @Autowired
    public TagsService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public ResponseEntity<TagsResponse> getTags()
    {
        List<Tags> allTags = tagsRepository.findAll();
        List<TagDTO> listOfTags = new TagsMapperImpl().tagsToTagsResponse(allTags);
        TagsResponse tagsResponse = TagsResponse.builder()
                .tags(listOfTags).build();
        return new ResponseEntity<>(tagsResponse, HttpStatus.OK);
    }
}
