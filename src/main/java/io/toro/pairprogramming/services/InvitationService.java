package io.toro.pairprogramming.services;

import java.util.List;

import io.toro.pairprogramming.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.toro.pairprogramming.models.Invitation;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.InvitationRepository;
import io.toro.pairprogramming.repositories.ProjectRepository;

import static io.toro.pairprogramming.models.InvitationStatus.ACCEPT;
import static io.toro.pairprogramming.models.InvitationStatus.DECLINE;
import static io.toro.pairprogramming.models.InvitationStatus.PENDING;

@Service
public class InvitationService {

    private InvitationRepository invitationRepository;

    private ProjectRepository projectRepository;

    private UserRepository userRepository;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository, ProjectRepository
            projectRepository, UserRepository userRepository) {
        this.invitationRepository = invitationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Invitation sendInvitation(Long projectId, String email, String message) {
        User user = userRepository.findByEmailContainingIgnoreCase(email);
        Project project = projectRepository.findOne(projectId);
        Invitation invitation = new Invitation();

        invitation.setMessage(message);
        invitation.setStatus(PENDING);
        invitation.setRecipient(user);
        invitation.setProject(project);

        return invitationRepository.save(invitation);
    }
    public Invitation acceptInvitation(Long invitationId){
        Invitation invitation = invitationRepository.findOne(invitationId);
        User recipient = userRepository.findOne(invitation.getRecipient().getIds());

        invitation.setStatus(ACCEPT);
        invitation.getProject().setProjectMember(recipient);

        return invitationRepository.save(invitation);
    }
    public Invitation declineInvitation(Long invitationId){
        Invitation invitation = invitationRepository.findOne(invitationId);

        invitation.setStatus(DECLINE);

        return invitationRepository.save(invitation);
    }

    public Invitation deleteInvitation(Long id) {
        Invitation invitation = invitationRepository.findOne( id );
        invitationRepository.delete( id );
        return invitation;
    }

    public Invitation findOne( Long id ) {
        return invitationRepository.findOne( id );
    }

    public List<Invitation> findAll() {
        return invitationRepository.findAll();
    }

    public List<Invitation> search( String search ) {
        return invitationRepository.findBySearchTermNamedNative( search );

    }

    public List<Invitation> findExisting(Long projectId, Long recipientId){
        return invitationRepository.findExistingInvitation(projectId,recipientId);
    }
}
