package io.toro.pairprogramming.models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class UserSkill extends AbstractEntity{

  private String name;
  private Integer level;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  //@JsonIgnoreProperties({"skills","projects"})
  @JsonIgnoreProperties("skills")
  private User user;
}
