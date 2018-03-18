package io.toro.pairprogramming.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.toro.pairprogramming.models.request.ProjectType;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import javax.persistence.Table;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "projects")
@JsonInclude(Include.NON_NULL)
public class Project extends AbstractEntity {

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "project",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ProjectSkill> skills = new ArrayList<>();


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Invitation> invitations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_email")
    @JsonIgnore
    private User activeEditor;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "project_members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> projectMembers = new HashSet<>();

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "repo_name")
    private String repoName;

    public ProjectType getType() {
            return ProjectType.getKey(typeId);
        }

    public void setType(ProjectType type) {
            typeId = type.getCode();
        }

    public void setInvitations( List<Invitation> invitations ) {
        if (invitations != null) {
            invitations.forEach(invitation -> invitation.setProject(this));
        }
        this.invitations = invitations;
    }

    public void setProjectMember(User user) {
        user.getProjects().add(this);
        projectMembers.add(user);
    }
}
