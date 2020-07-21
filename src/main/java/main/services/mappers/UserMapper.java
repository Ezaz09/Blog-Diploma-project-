package main.services.mappers;

import main.api.responses.UserResponse;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserMapper {
    @Autowired
    private ModelMapper modelMapper;

    public UserResponse toDto(User user){
        return Objects.isNull(user) ? null : modelMapper.map(user, UserResponse.class);
    }

}
