package io.toro.pairprogramming.services;

import java.util.List;

import io.toro.pairprogramming.models.request.InvitationRequest;
import io.toro.pairprogramming.repositories.UserSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    private UserSkillRepository userSkillRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserSkillRepository userSkillRepository) {
        this.userRepository = userRepository;
        this.userSkillRepository = userSkillRepository;
    }

    public List<User> findAll() {
        return (List<User>) this.userRepository.findAll();
    }

    public User getbyId(Long id){ return this.userRepository.findOne(id);}

    public List<User> match(Long id){
        return userRepository.findByProjectSkills( id );
    }
    public User findEmail(String email){
        return userRepository.findByEmail(email);
    }
}
