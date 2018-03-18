package io.toro.pairprogramming.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode( callSuper = true )
@Entity
@Data
@Table(name ="message")
public class Message extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name="project_id")
    @JsonIgnoreProperties(value = "user")
    private Project project;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonIgnoreProperties(value = "projects")
    private User user;

    @NotNull
    private String message;

    @CreationTimestamp
    private Date timestamp;

}
