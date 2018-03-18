package io.toro.pairprogramming.integration.utils;

import java.util.Arrays;
import java.util.List;

import io.toro.pairprogramming.models.ProjectSkill;

import com.github.javafaker.Faker;

public class SkillUtils {

    public static ProjectSkill createSkill() {
        Faker faker = new Faker();

        ProjectSkill projectSkill = new ProjectSkill();

        projectSkill.setName(faker.lorem().word());

        return projectSkill;
    }

    public static List<String> createSkills() {
        return Arrays.asList("php", "html", "js");
    }
}
