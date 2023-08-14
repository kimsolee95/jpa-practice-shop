package com.example.demo.featurepractice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamMemberDto {

  private String username;
  private int age;

  public TeamMemberDto(String username, int age) {
    this.username = username;
    this.age = age;
  }
}
