package io.toro.pairprogramming.models;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Setter;

@MappedSuperclass
public class AbstractEntity extends ResourceSupport {

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected Long id;

    @Getter
    @Setter
    @CreationTimestamp
    private Date createdAt;

    @Getter
    @Setter
    @UpdateTimestamp
    private Date updatedAt;

    @JsonIgnore
    public Long getIds() {
        return id;
    }
}
