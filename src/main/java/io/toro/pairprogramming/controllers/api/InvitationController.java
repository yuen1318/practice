package io.toro.pairprogramming.controllers.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import io.toro.pairprogramming.handlers.auth.ForbiddenActionException;
import io.toro.pairprogramming.handlers.invitation.InvitationIsAlreadyExistingException;
import io.toro.pairprogramming.handlers.invitation.InvitationNotFoundException;
import io.toro.pairprogramming.handlers.invitation.ProjectNotFoundException;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.services.UserService;
import io.toro.pairprogramming.services.projects.ProjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.toro.pairprogramming.models.Invitation;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.request.InvitationRequest;
import io.toro.pairprogramming.services.AuthService;
import io.toro.pairprogramming.services.InvitationService;

@RestController
@RequestMapping("api/v1/users")
public class InvitationController {

    private InvitationService invitationService;

    private ProjectManager projectManager;

    private UserService userService;

    private AuthService authService;

    @Autowired
    public InvitationController(InvitationService invitationService, ProjectManager
            projectManager, UserService userService, AuthService authService) {
        this.invitationService = invitationService;
        this.projectManager = projectManager;
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/invitations")
    public ResponseEntity search( HttpServletRequest request ) {
        if (request.getParameter("name") != null) {
            List<Invitation> specificInvitations = new ArrayList<>();
            for (Invitation invitation : invitationService.search(request.getParameter("name"))) {
                invitation = this.addInvitationLinks(invitation, request);
                specificInvitations.add(invitation);
            }
            return ResponseEntity.ok(specificInvitations);
        }
        else {
            List<Invitation> allInvitations = new ArrayList<>();
            for (Invitation invitation : invitationService.findAll()) {
                invitation = this.addInvitationLinks(invitation, request);
                allInvitations.add(invitation);
            }
            return ResponseEntity.ok().body(allInvitations);
        }
    }

    @GetMapping( "/{userId}/invitations/{invitationId}" )
    public ResponseEntity findById( @PathVariable Long id, HttpServletRequest request ) {
        Invitation invitation = invitationService.findOne( id );
        Map<String, Object> output = new HashMap<>();
        if ( invitation != null ) {
            output.put("id", invitation.getIds());
            output.put("body", invitation.getMessage());
            output.put("projectName",invitation.getProject().getName());
            output.put("email", invitation.getRecipient().getEmail( ));

            return ResponseEntity.ok(  ).body( output );
        }else
        output.put( "message", "No invitation found" );
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( output );
    }

    @PostMapping("/{userId}/projects/{projectId}/invite")
    public ResponseEntity sendInvitation(@PathVariable(value = "userId") Long userId,@PathVariable(value = "projectId") Long projectId, @RequestBody InvitationRequest invitationRequest, HttpServletRequest request) {
        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userService.getbyId(userId);
        Project project = projectManager.getById(projectId);
        User recipient = userService.findEmail(invitationRequest.getEmail());

        checkIfThisProjectIsExisting(project);
        checkIfUserOwnsThisProject(userToken, user);
        checkIfInvitationIsExisting(projectId, recipient);

        Invitation createdInvitation = invitationService.sendInvitation(projectId, invitationRequest.getEmail(), invitationRequest.getMessage());
        URI url = URI.create(request.getRequestURL() + "/" + createdInvitation.getId());

        createdInvitation = this.addInvitationLinks(createdInvitation, request);
        return ResponseEntity.created(url).body(createdInvitation);
        }

    @PutMapping("/{userId}/invitations/{invitationId}/accept")
    public ResponseEntity acceptInvitation(@PathVariable(value = "userId") Long userId,@PathVariable("invitationId") Long invitationId, HttpServletRequest request){
        Invitation invitation = invitationService.findOne(invitationId);
        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userService.getbyId(userId);

        checkIfUserOwnsThisInvitation(userToken, user);
        checkIfThisInvitationIsExisting(invitation);

        Invitation acceptedInvitation = invitationService.acceptInvitation(invitationId);


        URI url = URI.create( request.getRequestURL() + "/" + acceptedInvitation.getId() );
        acceptedInvitation = this.addInvitationLinks(acceptedInvitation,request);

        return ResponseEntity.created(url).body(acceptedInvitation);
    }

    @PutMapping("/{userId}/invitations/{invitationId}/decline")
    public ResponseEntity declineInvitation(@PathVariable(value = "userId") Long userId,@PathVariable("invitationId") Long invitationId, HttpServletRequest request){
        Invitation invitation = invitationService.findOne(invitationId);
        User userToken = authService.getUserFromToken(authService.getTokenFromHeader(request.getHeader("Authorization")));
        User user = userService.getbyId(userId);

        checkIfUserOwnsThisInvitation(userToken, user);
        checkIfThisInvitationIsExisting(invitation);

        Invitation declinedInvitation = invitationService.declineInvitation(invitationId);
        invitationService.deleteInvitation(declinedInvitation.getIds());
        URI url = URI.create( request.getRequestURL() + "/" + declinedInvitation.getId() );

        return ResponseEntity.created(url).body(declinedInvitation);
    }

    private Invitation addInvitationLinks(Invitation invitation, HttpServletRequest request) {
        String baseUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/invitations";

        String selfUrl = baseUrl + "/" + invitation.getIds();
        Link selfLink = new Link(selfUrl, "self");

        String skillUrl = baseUrl + "/" + invitation.getProject().getUser().getIds() + "/" + "skills";
        Link skillLink = new Link(skillUrl, "skills");

        String projectUrl = baseUrl + "/" + invitation.getProject().getUser().getIds() + "/" + "projects";
        Link projectLink = new Link(projectUrl, "projects");

        String recipientUrl = baseUrl + "/" + invitation.getRecipient().getIds();
        Link recipientLink = new Link(recipientUrl, "recipient");
        invitation.add(selfLink, skillLink, projectLink, recipientLink);

        return invitation;
    }

    private void checkIfInvitationIsExisting(@PathVariable(value = "projectId") Long projectId, User
            recipient) {
        List<Invitation> existingInvitations = invitationService.findExisting(projectId, recipient.getIds());
        if (existingInvitations.size() > 0)
            throw new InvitationIsAlreadyExistingException("Already Exists: Invitation");
    }

    private void checkIfUserOwnsThisProject(User userToken, User user) {
        if (userToken.getIds() != user.getIds()) {
            throw new ForbiddenActionException("Permission Denied: This is not your project");
        }
    }
    private void checkIfUserOwnsThisInvitation(User userToken, User user) {
        if (userToken.getIds() != user.getIds()) {
            throw new ForbiddenActionException("Permission Denied: This is not your invitation");
        }
    }

    private void checkIfThisProjectIsExisting(Project project) {
        if(project == null){
            throw new ProjectNotFoundException("Not Found: Project");
        }
    }
    private void checkIfThisInvitationIsExisting(Invitation invitation){
        if(invitation == null){
            throw new InvitationNotFoundException("Not Found: Invitation");
        }
    }
}
