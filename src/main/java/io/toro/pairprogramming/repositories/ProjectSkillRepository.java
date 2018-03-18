package io.toro.pairprogramming.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.toro.pairprogramming.models.ProjectSkill;

public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, Long> {

    @Query(value = "SELECT * FROM SKILLS ORDER BY RAND() LIMIT 3 OFFSET RAND() * 2", nativeQuery = true)
    Iterable<ProjectSkill> getSkillsRandom();

    ProjectSkill findByNameIgnoreCase(String name);
}
