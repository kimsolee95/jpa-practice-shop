package com.example.demo.featurepractice.repository;

import com.example.demo.featurepractice.entity.TeamMember;
import java.util.List;
import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TeamMemberJpaRepositoryTest {

  @Autowired
  EntityManager em;

  @Autowired
  TeamMemberJpaRepository teamMemberJpaRepository;

  @Test
  public void basicTest() {
    TeamMember teamMember = new TeamMember("member1", 10);
    teamMemberJpaRepository.save(teamMember);

    TeamMember findTeamMember = teamMemberJpaRepository.findById(teamMember.getId()).get();
    Assertions.assertThat(findTeamMember).isEqualTo(teamMember);

    List<TeamMember> result1 = teamMemberJpaRepository.findAll_Querydsl();
    Assertions.assertThat(result1).containsExactly(teamMember);

    List<TeamMember> result2 = teamMemberJpaRepository.findByUsername_Querydsl("member1");
    Assertions.assertThat(result2).containsExactly(teamMember);
  }


}