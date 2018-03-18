package io.toro.pairprogramming.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.UserSkill;

@Service
public class UserSkillsWeightMatcherService {

    public List<User> sortByLevelDesc(List<User> users, List<String> categories) {
        boolean isAllSorted = true;
        for(int i = 0; i < users.size() - 1; i++) {
            User user1 = users.get(i);
            User user2 = users.get(i + 1);
            if(!isSortedByLevel(user1, user2, categories)) {
                users = swapUsers(users, i, i + 1);
                isAllSorted = false;
            }
        }
        return !isAllSorted ? sortByLevelDesc(users, categories) : users;
    }

    private List<User> swapUsers(List<User> users, int user1Index, int user2Index) {
        User tempUser = users.get(user1Index);
        users.set(user1Index, users.get(user2Index));
        users.set(user2Index, tempUser);
        return users;
    }
    private Boolean isSortedByLevel(User user1, User user2, List<String> categories) {
        List<Integer> user1SkillLevels = this.getSkillLevels(user1.getSkills(), categories),
                user2SkillLevels = this.getSkillLevels(user2.getSkills(), categories);
        Integer user1Total = sumOfSkillLevels(user1SkillLevels),
                user2Total = sumOfSkillLevels(user2SkillLevels);
        if(user1Total == user2Total) {
            Integer user1Score = 0, user2Score = 0;
            Integer total = user1Total / categories.size();
            for(int i = 0; i < categories.size(); i++) {
                user1Score +=  Math.abs(user1SkillLevels.get(i) - total);
                user2Score += Math.abs(user2SkillLevels.get(i) - total);
            }
            return user1Score <= user2Score;
        }
        return user1Total > user2Total;
    }

    private Integer sumOfSkillLevels(List<Integer> skillLevels) {
        return skillLevels.stream().reduce((a, b) -> a+b).get();
    }

    private List<Integer> getSkillLevels(List<UserSkill> userSkills, List<String> skills) {
        List<Integer> skillLevels = new ArrayList<>();
        for (String skill : skills) {
            for(UserSkill userSkill : userSkills) {
                if(userSkill.getName().equals(skill)) {
                    skillLevels.add(userSkill.getLevel());
                }
            }
        }
        return skillLevels;
    }

}
