package io.toro.pairprogramming.integration.workshift;

import static io.toro.pairprogramming.integration.utils.AuthUtils.createUser;
import static io.toro.pairprogramming.integration.utils.WorkShiftUtils.createWorkShift;
import static io.toro.pairprogramming.integration.utils.WorkShiftUtils.createWorkShiftRequest;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

import io.toro.pairprogramming.integration.WithAuthenticationIntegrationTest;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.models.WorkShift;
import io.toro.pairprogramming.models.request.WorkShiftRequest;

public class WorkShiftIntegrationTest extends WithAuthenticationIntegrationTest {

    @Test
    public void shouldAddWorkShift() throws Exception {
        User user = entityManager.persistAndFlush( createUser() );
        mockMvc
                .perform(post("/api/v1/users/" + user.getIds() + "/workshifts")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content(objectMapper.writeValueAsString( createWorkShiftRequest() ))
                        .header("Authorization", "Bearer " + token ))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.timeZone").exists())
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists());
    }

    @Test
    public void shouldGetAllWorkShift() throws Exception {
        int i =10;
        while ( i --> 0){
            entityManager.persist( createWorkShift() );
        }
        entityManager.flush();

        mockMvc
                .perform(get("/api/v1/workshifts")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect( jsonPath("$.*", hasSize(10)) )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8_VALUE ));
    }

    @Test
    public void shouldGetWorkShiftById() throws Exception {
        WorkShift workShift = entityManager.persistAndFlush( createWorkShift() );
        mockMvc
                .perform(get("/api/v1/workshifts/" + workShift.getIds())
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.timeZone").exists())
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.endTime").exists())
                .andExpect( content().contentType( "application/hal+json;charset=UTF-8" ));
    }

    @Test
    public void shouldUpdateWorkShiftById() throws Exception {
        WorkShift workShift =  entityManager.persistAndFlush( createWorkShift() );

        WorkShiftRequest workShiftRequest = createWorkShiftRequest();
        workShiftRequest.setTimeZone( "yuen" );

        mockMvc
                .perform(put("/api/v1/workshifts/" + workShift.getIds())
                        .contentType( MediaType.APPLICATION_JSON )
                        .content(objectMapper.writeValueAsString( workShiftRequest ))
                        .header("Authorization", "Bearer " + token ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.timeZone").value( "yuen" ))
                .andExpect( content().contentType( "application/hal+json;charset=UTF-8"));
    }

}
