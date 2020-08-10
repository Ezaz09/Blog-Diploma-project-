package main.controller;

import main.api.responses.TagsResponse;
import main.services.TagsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTagController {
    private TagsService tagsService;

    public ApiTagController(TagsService tagsService)
    {
        this.tagsService = tagsService;
    }

    @GetMapping(path = "/api/tag")
    public ResponseEntity<TagsResponse> listOfTags()
    {
        return tagsService.getTags();
    }
}
