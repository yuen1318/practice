package io.toro.pairprogramming.integration.message;


import static io.toro.pairprogramming.integration.utils.AuthUtils.createToken;
import static io.toro.pairprogramming.integration.utils.AuthUtils.createUser;
import static io.toro.pairprogramming.integration.utils.MessageUtils.createMessage;
import static io.toro.pairprogramming.integration.utils.ProjectUtils.createProject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

import io.toro.pairprogramming.integration.BaseIntegrationTest;
import io.toro.pairprogramming.models.Message;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;

public class MessageControllerTest extends BaseIntegrationTest {

    @Test
    public void shouldSendMessage() throws Exception {
        //createRepo user
        User user = entityManager.persistAndFlush(createUser());
        String subject = objectMapper.writeValueAsString(user);
        String token = createToken(subject, secret);
        //createRepo project
        Project dummyProject = createProject();
        dummyProject.setUser( user);
        Project project = entityManager.persistAndFlush( dummyProject );
        //createRepo message
        Message message = createMessage(project,user);
        System.out.println(objectMapper.writeValueAsString( project ));
        mockMvc
                .perform(post("/api/v1/projects/"+project.getIds()+"/messages")
                        .accept( "application/json" )
                        .content(message.getMessage())
                        .header("Authorization", "Bearer " + token ))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.project").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void shouldGetMessageByProjectId() throws Exception {
        //createRepo user
        User user = entityManager.persistAndFlush(createUser());
        String subject = objectMapper.writeValueAsString(user);
        String token = createToken(subject, secret);
        //createRepo project
        Project dummyProject = createProject();
        dummyProject.setUser( user);
        Project project = entityManager.persistAndFlush( dummyProject );

        entityManager.merge(createMessage(project,user) );

        mockMvc
                .perform(get("/api/v1/projects/"+project.getIds()+"/messages")
                    .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ));
    }
}
