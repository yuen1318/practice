package io.toro.pairprogramming.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import io.toro.pairprogramming.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailContainingIgnoreCase(String email);

    User findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(value = "SELECT u.* "
            + "FROM USERS u "
            + "LEFT JOIN USER_SKILL u_s "
            + "ON u.id = u_s.user_id "
            + "LEFT JOIN LANGUAGE_USER l_u "
            + "ON u.id = l_u.user_id "
            + "WHERE name "
            + "IN (SELECT name "
            + "FROM project_skill "
            + "WHERE project_id = :projectId ) "
            + "AND language_id "
            + "IN (SELECT language_id "
            + "FROM language_user "
            + "WHERE user_id = (SELECT user_id "
            + "FROM projects "
            + "WHERE projects.id = :projectId)) "
            + "GROUP BY u_s.user_id "
            + "HAVING COUNT(*) >= (SELECT COUNT(*) "
            + "FROM project_skill "
            + "WHERE project_id = :projectId)"
            , nativeQuery = true)
    List<User> findByProjectSkills(@Param(value = "projectId") Long projectId);
}
