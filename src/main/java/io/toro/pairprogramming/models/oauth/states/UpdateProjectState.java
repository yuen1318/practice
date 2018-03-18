package io.toro.pairprogramming.models.oauth.states;

public class UpdateProjectState extends ProjectState {

    private String oldName;

    public String getOldRepoName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }
}
