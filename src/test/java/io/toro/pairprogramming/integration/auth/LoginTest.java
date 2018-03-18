package io.toro.pairprogramming.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.toro.pairprogramming.integration.BaseIntegrationTest;
import io.toro.pairprogramming.integration.utils.AuthUtils;
import io.toro.pairprogramming.models.User;

public class LoginTest extends BaseIntegrationTest {

    @Test
    public void shouldLoginUser() throws Exception {
        User user = AuthUtils.createUser();
        String password = faker.lorem().word();

        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

        entityManager.persistAndFlush(user);

        HashMap<String, String> credentials = new HashMap<>();

        credentials.put("email", user.getEmail());
        credentials.put("password", password);

        mockMvc
                .perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(credentials)))
                .andDo(print())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    public void shouldReturnErrorWhenLoginFailed() throws Exception {
        User user = AuthUtils.createUser();
        String password = faker.lorem().word();

        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));

        entityManager.persistAndFlush(user);

        HashMap<String, String> credentials = new HashMap<>();

        credentials.put("email", user.getEmail());
        credentials.put("password", faker.book().title());

        mockMvc
                .perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(credentials)))
                .andDo(print())
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("message").exists());
    }
}
