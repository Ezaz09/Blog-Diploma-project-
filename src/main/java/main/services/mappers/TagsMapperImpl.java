package main.services.mappers;

import main.api.responses.TagDTO;
import main.model.Tag;
import main.model.Tag2Post;
import main.model.repositories.Tag2PostRepository;
import main.model.repositories.TagsRepository;

import java.util.ArrayList;
import java.util.List;

public class TagsMapperImpl {
    public List<TagDTO> tagsToTagsResponse(List<Tag> tags)
    {
        if( tags == null )
        {
            return null;
        }

        List<TagDTO> list = new ArrayList<>(tags.size());
        for ( Tag tag1 : tags ) {
            list.add( tagToTagDTO(tag1) );
        }

        return list;
    }

    public TagDTO tagToTagDTO(Tag tag)
    {
        if( tag == null)
        {
            return null;
        }

        TagDTO tagDTO = new TagDTO();
        tagDTO.setName(tag.getName());
        tagDTO.setWeight(0);
        return tagDTO;
    }

}
