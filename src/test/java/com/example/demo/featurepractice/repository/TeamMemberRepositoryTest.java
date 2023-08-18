package com.example.demo.featurepractice.repository;

import com.example.demo.featurepractice.dto.MemberSearchCondition;
import com.example.demo.featurepractice.dto.MemberTeamDto;
import com.example.demo.featurepractice.entity.Team;
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
public class TeamMemberRepositoryTest {

  @Autowired
  EntityManager em;

  @Autowired TeamMemberRepository teamMemberRepository;

  @Test
  public void basicTest() {
    TeamMember teamMember = new TeamMember("member1", 10);
    teamMemberRepository.save(teamMember);

    TeamMember findTeamMember = teamMemberRepository.findById(teamMember.getId()).get();
    Assertions.assertThat(findTeamMember).isEqualTo(teamMember);

    List<TeamMember> result1 = teamMemberRepository.findAll();
    Assertions.assertThat(result1).containsExactly(teamMember);

    List<TeamMember> result2 = teamMemberRepository.findByUsername("member1");
    Assertions.assertThat(result2).containsExactly(teamMember);
  }

  @Test
  public void searchTest() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    TeamMember member1 = new TeamMember("member1", 10, teamA);
    TeamMember member2 = new TeamMember("member2", 20, teamB);
    TeamMember member3 = new TeamMember("member3", 30, teamA);
    TeamMember member4 = new TeamMember("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    MemberSearchCondition condition = new MemberSearchCondition();
    condition.setAgeGoe(35);
    condition.setAgeLoe(40);
    condition.setTeamName("teamB");

    List<MemberTeamDto> result = teamMemberRepository.search(condition);
    Assertions.assertThat(result).extracting("username").containsExactly("member4");
  }

}
