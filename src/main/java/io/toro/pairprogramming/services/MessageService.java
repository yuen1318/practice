package io.toro.pairprogramming.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.toro.pairprogramming.handlers.project.InvalidInputException;
import io.toro.pairprogramming.handlers.project.ProjectAlreadyExistException;
import io.toro.pairprogramming.handlers.project.ProjectEmptyException;
import io.toro.pairprogramming.models.Message;
import io.toro.pairprogramming.models.Project;
import io.toro.pairprogramming.models.User;
import io.toro.pairprogramming.repositories.MessageRepository;
import io.toro.pairprogramming.repositories.ProjectRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MessageService {

    private MessageRepository messageRepository;
    private ProjectRepository projectRepository;

    @Autowired
    public MessageService( MessageRepository messageRepository,
            ProjectRepository projectRepository ) {
        this.messageRepository = messageRepository;
        this.projectRepository = projectRepository;
    }

    public Message sendMessage(Long projectId, String message, User user)   {
        Project project = projectRepository.findOne( projectId );
        if(project != null){
            try {
                ObjectMapper mapper = new ObjectMapper();
                HashMap<String, String> messageBody  = mapper.readValue(message,new TypeReference<HashMap<String, String>>() {});
                Message msg = new Message();
                msg.setProject(project);
                msg.setUser( user );
                msg.setMessage(messageBody.get( "message" ));
                return  messageRepository.save( msg );
            } catch (  IOException  e ) {
                throw new InvalidInputException();
            }
        }else{
            throw new ProjectEmptyException();
        }
    }

    public List<Message> getAllMessageByProjectId(Long projectId){
        Project project = projectRepository.findOne(projectId);
        if(project != null){
            return messageRepository.findAllMessageByProjectIdOrderByTimestampAsc( project.getIds() );
        }else{
            throw new ProjectEmptyException();
        }
    }
}
