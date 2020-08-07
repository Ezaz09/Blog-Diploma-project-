package main.services.mappers;

import main.api.responses.PostDTO;
import main.api.responses.TagDTO;
import main.model.Posts;
import main.model.Tags;

import java.util.ArrayList;
import java.util.List;

public class TagsMapperImpl {
    public List<TagDTO> tagsToTagsResponse(List<Tags> tags)
    {
        if( tags == null )
        {
            return null;
        }

        List<TagDTO> list = new ArrayList<TagDTO>( tags.size() );
        for ( Tags tags1 : tags ) {
            list.add( tagToTagDTO( tags1 ) );
        }

        return list;
    }

    public TagDTO tagToTagDTO(Tags tag)
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
