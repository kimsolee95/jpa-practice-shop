package com.example.demo.featurepractice.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@ToString(of = {"id", "username"})
public class Team {

  @Id @GeneratedValue
  private Long id;
  private String name;

  @OneToMany(mappedBy = "team")
  List<TeamMember> members = new ArrayList<>();

  public Team(String name) {
    this.name = name;
  }
}
