package io.toro.pairprogramming.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.toro.pairprogramming.models.Invitation;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query(value= "SELECT * "
            + "FROM invitations i "
            + "LEFT JOIN users u "
            + "ON i.id = u.id "
            + "LEFT JOIN projects p "
            + "ON i.id = p.id"
            + "WHERE LOWER(p.name) "
            + "LIKE LOWER(CONCAT('%',:search,'%')) "
            + "OR LOWER(users.email) LIKE LOWER(CONCAT('%',:search,'%'))",
            nativeQuery = true)
    List<Invitation> findBySearchTermNamedNative(@Param("search")String search);

    @Query(value="SELECT * " +
            "FROM INVITATIONS i " +
            "LEFT JOIN PROJECTS p " +
            "ON i.project_id = p.id " +
            "LEFT JOIN USERS u " +
            "ON i.recipient_id = u.id " +
            "WHERE i.project_id = :projectId " +
            "AND i.recipient_id = :recipientId",
    nativeQuery = true)
    List<Invitation> findExistingInvitation(@Param(value="projectId")Long projectId,
                                            @Param(value = "recipientId")Long recipientId );
}
