package io.toro.pairprogramming.integration.auth;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.toro.pairprogramming.integration.BaseIntegrationTest;
import io.toro.pairprogramming.integration.utils.AuthUtils;
import io.toro.pairprogramming.models.User;

public class RegisterTest extends BaseIntegrationTest {

    @Test
    public void shouldRegisterUser() throws Exception {
        User user = AuthUtils.createUser();

        mockMvc
                .perform(post("/api/v1/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(jsonPath("$.firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(user.getLastName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    public void shouldReturnErrorWhenEmailExists() throws Exception {
        User user = AuthUtils.createUser();

        String password = user.getPassword();

        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

        entityManager.persistAndFlush(user);

        user.setPassword(password);

        mockMvc
                .perform(post("/api/v1/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").exists());
    }
}
