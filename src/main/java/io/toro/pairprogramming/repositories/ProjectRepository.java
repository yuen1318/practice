package io.toro.pairprogramming.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import io.toro.pairprogramming.models.Project;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(value ="SELECT * "
            + "FROM projects p "
            + "LEFT JOIN project_skill p_s "
            + "ON p.id = p_s.project_id "
            + "LEFT JOIN skills s "
            + "ON s.id = p_s.skill_id "
            + "WHERE LOWER(s.name) "
            + "LIKE LOWER(CONCAT('%',:search,'%')) "
            + "OR LOWER(p.project_name) LIKE LOWER(CONCAT('%',:search,'%'))",
            nativeQuery = true)
    List<Project> findBySearchTermNamedNative(@Param("search")String search);

    List<Project> findByUserId(Long id);


    @Query(value = "SELECT p.* "
            + "FROM PROJECTS p "
            + "LEFT JOIN PROJECT_SKILL p_s "
            + "ON p.id = p_s.project_id "
            + "LEFT JOIN USER u "
            + "ON u.id = p.user_id "
            + "LEFT JOIN LANGUAGE_USER l_u "
            + "ON p.user_id = l_u.user_id"
            + "WHERE skill_id "
            + "IN (SELECT skill_id "
            + "FROM user_skill "
            + "WHERE user_id = :userId ) "
            + "AND language_id "
            + "IN (SELECT language_id "
            + "FROM language_user "
            + "WHERE user_id = :userId) "
            + "GROUP BY u_s.user_id "
            + "HAVING COUNT(*) >= (SELECT COUNT(*) "
            + "FROM user_skill "
            + "WHERE user_id = :userId)"
            , nativeQuery = true)
    List<Project> findByUserSkills(@Param(value = "userId") Long userId);

}
