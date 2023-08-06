package com.example.demo.featurepractice;

import com.example.demo.featurepractice.entity.QTeam;
import com.example.demo.featurepractice.entity.QTeamMember;
import com.example.demo.featurepractice.entity.Team;
import com.example.demo.featurepractice.entity.TeamMember;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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

  @Test
  public void search() {
    TeamMember findTeamMember = queryFactory
        .selectFrom(QTeamMember.teamMember)
        .where(QTeamMember.teamMember.username.eq("member1")
            .and(QTeamMember.teamMember.age.eq(10)))
        .fetchOne();
    Assertions.assertThat(findTeamMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void searchAndParam() {
    TeamMember findTeamMember = queryFactory
        .selectFrom(QTeamMember.teamMember)
        .where(
//            QTeamMember.teamMember.username.eq("member1")
//            .and(QTeamMember.teamMember.age.between(10, 30))
            QTeamMember.teamMember.username.eq("member1"),
            QTeamMember.teamMember.age.eq(10)
        )
        .fetchOne();
    Assertions.assertThat(findTeamMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void resultFetch() {
//    List<TeamMember> fetch = queryFactory
//        .selectFrom(QTeamMember.teamMember)
//        .fetch();
//    TeamMember fetchOne = queryFactory
//        .selectFrom(QTeamMember.teamMember)
//        .fetchOne();
//    TeamMember fetchFirst = queryFactory
//        .selectFrom(QTeamMember.teamMember)
//        .fetchFirst();
    QueryResults<TeamMember> results = queryFactory
        .selectFrom(QTeamMember.teamMember)
        .fetchResults();
    results.getTotal();
    List<TeamMember> content = results.getResults();

    long total = queryFactory
        .selectFrom(QTeamMember.teamMember)
        .fetchCount();
  }

  /**
   * 회원 정렬
   * 1. 회원 나이 내림차순
   * 2. 회원 이름 올림차순
   * 단, 회원 이름 없을 시, 마지막에 출력(null last)
   * */
  @Test
  public void sort() {
    em.persist(new TeamMember(null, 100));
    em.persist(new TeamMember("member5", 100));
    em.persist(new TeamMember("member6", 100));

    List<TeamMember> result = queryFactory
        .selectFrom(QTeamMember.teamMember)
        .where(QTeamMember.teamMember.age.eq(100))
        .orderBy(
            QTeamMember.teamMember.age.desc(),
            QTeamMember.teamMember.username.asc().nullsLast()
            )
        .fetch();

    TeamMember member5 = result.get(0);
    TeamMember member6 = result.get(1);
    TeamMember memberNull = result.get(2);
    Assertions.assertThat(member5.getUsername()).isEqualTo("member5");
    Assertions.assertThat(member6.getUsername()).isEqualTo("member6");
    Assertions.assertThat(memberNull.getUsername()).isNull();
  }

  @Test
  public void paging1() {
    List<TeamMember> result = queryFactory
        .selectFrom(QTeamMember.teamMember)
        .orderBy(QTeamMember.teamMember.username.desc())
        .offset(1)
        .limit(2)
        .fetch();
    Assertions.assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void paging2() {
    QueryResults<TeamMember> queryResults = queryFactory
        .selectFrom(QTeamMember.teamMember)
        .orderBy(QTeamMember.teamMember.username.desc())
        .offset(1)
        .limit(2)
        .fetchResults();
    Assertions.assertThat(queryResults.getTotal()).isEqualTo(4);
    Assertions.assertThat(queryResults.getLimit()).isEqualTo(2);
    Assertions.assertThat(queryResults.getOffset()).isEqualTo(1);
    Assertions.assertThat(queryResults.getResults()).isEqualTo(2);
  }

}
