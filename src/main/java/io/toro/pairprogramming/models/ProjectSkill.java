package io.toro.pairprogramming.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class ProjectSkill extends AbstractEntity {

    private String name;
    private Integer level;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    //@JsonIgnoreProperties({"project", "activeEditor"})
    @JsonIgnore
    private Project project;
}
