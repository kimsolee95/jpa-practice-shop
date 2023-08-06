package com.example.demo.featurepractice;

import com.example.demo.featurepractice.entity.QTeamMember;
import com.example.demo.featurepractice.entity.Team;
import com.example.demo.featurepractice.entity.TeamMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

  @Autowired
  EntityManager em;

  JPAQueryFactory queryFactory;

  @BeforeEach
  public void before() {
    queryFactory = new JPAQueryFactory(em);
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    TeamMember member1 = new TeamMember("member1", 10, teamA);
    TeamMember member2 = new TeamMember("member2", 20, teamB);
    TeamMember member3 = new TeamMember("member3", 10, teamA);
    TeamMember member4 = new TeamMember("member4", 20, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);
  }

  @Test
  public void startJPQL() {
    //member1

    String qlString =
        "select tm from TeamMember tm "
        + "where tm.username = :username";
    TeamMember findTeamMember = em.createQuery(qlString, TeamMember.class)
        .setParameter("username", "member1")
        .getSingleResult();

    Assertions.assertThat(findTeamMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void startQuerydsl() {
    QTeamMember tm = new QTeamMember("tm");
    TeamMember findTeamMember = queryFactory
        .select(tm)
        .from(tm)
        .where(tm.username.eq("member1"))
        .fetchOne();
    Assertions.assertThat(findTeamMember.getUsername()).isEqualTo("member1");
  }

}
