package io.toro.pairprogramming.models;

import java.time.Duration;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name ="workshift")
public class WorkShift  extends  AbstractEntity {

    @NotBlank
    private String timeZone;
    @NotNull
    private Date startTime;
    @NotNull
    private Date endTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(value = "workShift")
    private User user;

    @JsonIgnore
    public Long getDuration(){
        return Duration
                .between(
                        new java.sql.Timestamp( getStartTime().getTime() ).toLocalDateTime(),
                        new java.sql.Timestamp( getEndTime().getTime() ).toLocalDateTime())
                .getSeconds();
    }

}
