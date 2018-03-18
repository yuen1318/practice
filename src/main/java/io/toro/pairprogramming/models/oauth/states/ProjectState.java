package io.toro.pairprogramming.models.oauth.states;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectState {

    public enum Process {
        @JsonProperty("create") CREATE,

        @JsonProperty("import") IMPORT,

        @JsonProperty("updateProject") UPDATE
    }

    /**
     * This is the id of resource owner
     */
    private String id;

    /**
     * This is the id of the related project
     */
    private Long projectId;

    /**
     * This is what the app was trying to do
     */
    private Process process;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
