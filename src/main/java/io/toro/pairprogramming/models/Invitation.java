package io.toro.pairprogramming.models;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.JoinColumn;


import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Getter
@Setter
@ToString
@Table(name = "invitations")
public class Invitation extends AbstractEntity{

    @NotBlank
    private String message;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    @JsonIgnore
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    @Column(name = "status_id")
    private Integer statusId;

    public InvitationStatus getStatus() {
        return InvitationStatus.getKey(statusId);
    }

    public void setStatus(InvitationStatus status) {
        statusId = status.getCode();
    }


}
