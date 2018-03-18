package io.toro.pairprogramming.integration.utils;

import com.github.javafaker.Faker;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.request.ProjectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProjectUtils {


    public static List<Project> createProjects() {
        List<Project> projects = new ArrayList<>();

        Integer count = (new Random()).nextInt(10) + 1;

        while (count --> 0) {
            projects.add(createProject());
        }

        return projects;
    }

    public static Project createProject() {
        Faker faker = new Faker();

        Random random = new Random();

        Project project = new Project();

        project.setName(faker.book().title());

        ProjectType type = ProjectType.values()[random.nextInt(ProjectType.values().length)];

        project.setType(type);

        return project;
    }
}
