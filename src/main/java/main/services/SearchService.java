package main.services;

import main.model.User;
import main.model.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final UsersRepository usersRepository;

    @Autowired
    public SearchService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User findUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
}
