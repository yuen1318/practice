package io.toro.pairprogramming.repositories;

import io.toro.pairprogramming.models.ProjectSkill;
import io.toro.pairprogramming.models.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long>{

  @Query(value = "SELECT * FROM SKILLS ORDER BY RAND() LIMIT 3 OFFSET RAND() * 2", nativeQuery = true)
  Iterable<UserSkill> getSkillsRandom();

  UserSkill findByNameIgnoreCase(String name);
}
