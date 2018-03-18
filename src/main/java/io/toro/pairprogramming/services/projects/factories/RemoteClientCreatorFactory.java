package io.toro.pairprogramming.services.projects.factories;

import io.toro.pairprogramming.handlers.ClientNotFound;
import io.toro.pairprogramming.models.request.ProjectType;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RemoteClientCreatorFactory {

    private HashMap<ProjectType,RemoteClientCreator> creators = new HashMap<>();

    public RemoteClientCreator getCreator(ProjectType type) throws ClientNotFound {
        if (creators.containsKey(type)) {
            return creators.get(type);
        }

        throw new ClientNotFound("No remote client found for " + type.name());
    }

    public void addCreator(ProjectType type, RemoteClientCreator creator) {
        creators.put(type,creator);
    }
}
