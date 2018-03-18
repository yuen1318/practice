package io.toro.pairprogramming.models.request;

import io.toro.pairprogramming.models.request.Content;

public class MoveContent  extends Content {

    private String oldName;

    private String oldParent;

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getOldParent() {
        return oldParent;
    }

    public void setOldParent(String oldParent) {
        this.oldParent = oldParent;
    }
}
