package com.example.demo.featurepractice.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@ToString(of = { "id", "username", "age"})
public class TeamMember {

  @Id @GeneratedValue
  @Column(name = "team_member_id")
  private Long id;
  private String username;
  private int age;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id")
  private Team team;

  public TeamMember(String username) {
    this(username, 0);
  }

  public TeamMember(String username, int age) {
    this(username, age, null);
  }

  public TeamMember(String username, int age, Team team) {
    this.username = username;
    this.age = age;
    if (team!= null) {
      changeTeam(team);
    }
  }

  public void changeTeam(Team team) {
    this.team = team;
    team.getMembers().add(this);
  }
}
