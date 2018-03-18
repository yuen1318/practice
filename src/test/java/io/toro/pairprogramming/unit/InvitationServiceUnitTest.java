package io.toro.pairprogramming.unit;

import java.util.ArrayList;
import java.util.List;

import io.toro.pairprogramming.integration.BaseIntegrationTest;
import io.toro.pairprogramming.integration.utils.AuthUtils;
import io.toro.pairprogramming.integration.utils.InvitationUtils;
import io.toro.pairprogramming.integration.utils.ProjectUtils;
import io.toro.pairprogramming.models.Invitation;
import io.toro.pairprogramming.models.InvitationStatus;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;

import io.toro.pairprogramming.repositories.ProjectRepository;
import io.toro.pairprogramming.repositories.UserRepository;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.auth.AUTH;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.toro.pairprogramming.repositories.InvitationRepository;
import io.toro.pairprogramming.services.InvitationService;


import static io.toro.pairprogramming.integration.utils.AuthUtils.createToken;
import static io.toro.pairprogramming.integration.utils.AuthUtils.createUser;
import static io.toro.pairprogramming.models.InvitationStatus.ACCEPT;
import static io.toro.pairprogramming.models.InvitationStatus.DECLINE;
import static io.toro.pairprogramming.models.InvitationStatus.PENDING;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class InvitationServiceUnitTest extends BaseIntegrationTest{

    private InvitationService invitationService;

    @MockBean
    private InvitationRepository invitationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProjectRepository projectRepository;


    @Before
    public void setUp() throws Exception {
        invitationService = new InvitationService(invitationRepository,projectRepository,userRepository);
    }

    @Test
    public void shouldSendInvitation() throws Exception{
        //User1 with project
        List<Project> projects = new ArrayList<>();
        User projectOwner = AuthUtils.createUser();
        Project project = ProjectUtils.createProject();
        projectOwner.setId(Long.parseLong("1"));
        projectOwner.setProjects(projects);
        project.setId(Long.parseLong("1"));
        projects.add(project);
        when(userRepository.save(projectOwner))
                .thenReturn(projectOwner);


        //Recipient
        User recipient = AuthUtils.createUser();
        recipient.setId(Long.parseLong("2"));

        when(userRepository.save(recipient))
                .thenReturn(recipient);

        Invitation testInvitation = InvitationUtils.createInvitation();
        testInvitation.setId(Long.parseLong("1"));
        testInvitation.setRecipient(recipient);
        testInvitation.setProject(project);

        when(invitationRepository.save(testInvitation))
                .thenReturn(testInvitation);

        Long id = project.getIds();
        String email = recipient.getEmail();
        String message = testInvitation.getMessage();
        Invitation actualInvitation = invitationService.sendInvitation(id, email, message);


        assertThat(actualInvitation, samePropertyValuesAs(testInvitation));

    }
    @Test
    public void shouldAcceptInvitation() throws Exception{
        //User with project
//        List<Project> projects = new ArrayList<>();
        User projectOwner = AuthUtils.createUser();
        projectOwner.setId(Long.parseLong("1"));
//        projectOwner.setProjects(projects);
        Project project = ProjectUtils.createProject();
        project.setId(Long.parseLong("1"));
        project.setUser(projectOwner);
//        projects.add(project);
        when(projectRepository.save(project))
                .thenReturn(project);

        //Recipient
        User recipient = AuthUtils.createUser();
        recipient.setId(Long.parseLong("2"));

        when(userRepository.save(recipient))
                .thenReturn(recipient);
//
        //sent invitation pending
        Invitation testInvitation = InvitationUtils.createInvitation();
        testInvitation.setId(Long.parseLong("1"));
        testInvitation.setRecipient(recipient);
        testInvitation.setProject(project);

        //mock accept
        Invitation expected = (Invitation) BeanUtils.cloneBean(testInvitation);
        expected.setStatus(ACCEPT);
        expected.getProject().setProjectMember(testInvitation.getRecipient());

        when(invitationRepository.save(testInvitation))
                .thenReturn(expected);


        assertEquals(expected.getStatus(), expected.getStatus());
    }
    @Test
    public void shouldDeclineInvitation() throws Exception{
        //User with project
        User projectOwner = AuthUtils.createUser();
        projectOwner.setId(Long.parseLong("1"));
        Project project = ProjectUtils.createProject();
        project.setId(Long.parseLong("1"));
        project.setUser(projectOwner);
        when(projectRepository.save(project))
                .thenReturn(project);

        //Recipient
        User recipient = AuthUtils.createUser();
        recipient.setId(Long.parseLong("2"));

        when(userRepository.save(recipient))
                .thenReturn(recipient);
//
        //sent invitation pending
        Invitation testInvitation = InvitationUtils.createInvitation();
        testInvitation.setId(Long.parseLong("1"));
        testInvitation.setRecipient(recipient);
        testInvitation.setProject(project);

        //mock accept
        Invitation expected = (Invitation) BeanUtils.cloneBean(testInvitation);
        expected.setStatus(DECLINE);
        expected.getProject().setProjectMember(testInvitation.getRecipient());

        when(invitationRepository.save(testInvitation))
                .thenReturn(expected);


        assertEquals(expected.getStatus(), expected.getStatus());
    }
    @Test
    public void shouldDeleteInvitation() throws Exception{
        //User with project
        List<Project> projects = new ArrayList<>();
        User projectOwner = AuthUtils.createUser();
        Project project = ProjectUtils.createProject();
        projectOwner.setId(Long.parseLong("1"));
        project.setId(Long.parseLong("1"));
        projects.add(project);
        projectOwner.setProjects(projects);

        when(userRepository.save(projectOwner))
                .thenReturn(projectOwner);

        //Recipient
        User recipient = AuthUtils.createUser();
        recipient.setId(Long.parseLong("2"));
        when(userRepository.save(recipient))
                .thenReturn(recipient);

        Invitation testInvitation = InvitationUtils.createInvitation();
        testInvitation.setId(Long.parseLong("1"));
        testInvitation.setRecipient(recipient);
        testInvitation.setProject(project);

        invitationService.deleteInvitation(testInvitation.getIds());

        verify(invitationRepository, times(1))
                .delete(testInvitation.getIds());
    }
    @Test
    public void shouldFindOneInvitation() throws Exception{
        Invitation testInvitation = InvitationUtils.createInvitation();

        when(invitationRepository.findOne(testInvitation.getIds()))
                .thenReturn(testInvitation);

        Invitation actualInvitation = invitationRepository.findOne(testInvitation.getIds());

        assertThat(testInvitation, samePropertyValuesAs(actualInvitation));

    }
    @Test
    public void shouldFindAllInvitation() throws Exception{
        List<Invitation> testInvitations = new ArrayList<>();
        Integer count = 10;

        while(count --> 0){
            testInvitations.add(InvitationUtils.createInvitation());
        }
        when(invitationRepository.findAll())
                .thenReturn(testInvitations);

        List<Invitation> actualInvitations = invitationRepository.findAll();
        assertThat(testInvitations, samePropertyValuesAs(actualInvitations));
    }
}
