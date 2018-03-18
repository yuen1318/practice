package io.toro.pairprogramming.controllers.api;

import java.util.List;

import io.toro.pairprogramming.models.Language;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.LanguageRepository;
import io.toro.pairprogramming.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserLanguageController {

    private UserRepository userRepository;
    private LanguageRepository languageRepository;

    @Autowired
    public UserLanguageController(UserRepository userRepository, LanguageRepository
            languageRepository) {
        this.userRepository = userRepository;
        this.languageRepository = languageRepository;
    }
    @PostMapping("/{id}/languages")
    public ResponseEntity addLanguage(@PathVariable("id") Long id, @RequestBody List<String> languages){
        User user = userRepository.findOne(id);
        for(String language : languages){
            Language l = languageRepository.findByNameIgnoreCase(language);
            if(l == null){
                l = new Language();
                l.setName(language);
                l = languageRepository.save(l);
                user.getLanguages().add(l);
                userRepository.save(user);
                continue;
            }
            user.getLanguages().add(l);
            userRepository.save(user);
        }
        return ResponseEntity.ok().body(user);
    }
    @DeleteMapping("/{id}/languages")
    public ResponseEntity deleteLanguage(@PathVariable("id") Long id, @RequestBody List<String> languages){
        User user = userRepository.findOne(id);
        for(String language : languages){
            Language l = languageRepository.findByNameIgnoreCase(language);
            if(l == null){
                continue;
            }
            user.getSkills().remove(l);
            userRepository.save(user);
        }
        return ResponseEntity.ok().body(userRepository.save(user));
    }
}
