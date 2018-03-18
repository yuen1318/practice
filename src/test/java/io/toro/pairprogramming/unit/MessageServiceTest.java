package io.toro.pairprogramming.unit;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.toro.pairprogramming.integration.utils.AuthUtils;
import io.toro.pairprogramming.integration.utils.MessageUtils;
import io.toro.pairprogramming.integration.utils.ProjectUtils;
import io.toro.pairprogramming.models.Message;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.MessageRepository;

@RunWith(SpringRunner.class)
public class MessageServiceTest {

    @MockBean
    private MessageRepository messageRepository;

    @Test
    public void shouldSendMessage() throws Exception {
        User user = AuthUtils.createUser();
        Project project = ProjectUtils.createProject();
        Message result = MessageUtils.createMessage( project,user );
        when(messageRepository.save(result))
                .thenReturn(result);
    }

    @Test
    public void shouldGetAllMessageByProjectId() throws Exception {
        List<Message> messages = new ArrayList<>();

        for(int i=0; i<10; i++){
            User user = AuthUtils.createUser();
            Project project = ProjectUtils.createProject();
            messages.add( MessageUtils.createMessage( project,user ));
        }

        List<Message> result = messageRepository.findAllMessageByProjectIdOrderByTimestampAsc( anyLong() );
        when(messageRepository.findAllMessageByProjectIdOrderByTimestampAsc( anyLong() ))
                .thenReturn( result );
    }
}
