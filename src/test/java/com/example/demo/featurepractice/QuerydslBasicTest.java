package com.example.demo.featurepractice;

import static com.example.demo.featurepractice.entity.QTeam.team;
import static com.example.demo.featurepractice.entity.QTeamMember.teamMember;

import com.example.demo.featurepractice.dto.QTeamMemberDto;
import com.example.demo.featurepractice.dto.TeamMemberDto;
import com.example.demo.featurepractice.dto.UserDto;
import com.example.demo.featurepractice.entity.QTeam;
import com.example.demo.featurepractice.entity.QTeamMember;
import com.example.demo.featurepractice.entity.Team;
import com.example.demo.featurepractice.entity.TeamMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

  @Autowired
  EntityManager em;

  JPAQueryFactory queryFactory;

  @BeforeEach
  @Commit
  public void before() {
    queryFactory = new JPAQueryFactory(em);
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
        .selectFrom(teamMember)
        .where(teamMember.username.eq("member1")
            .and(teamMember.age.eq(10)))
        .fetchOne();
    Assertions.assertThat(findTeamMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void searchAndParam() {
    TeamMember findTeamMember = queryFactory
        .selectFrom(teamMember)
        .where(
//            QTeamMember.teamMember.username.eq("member1")
//            .and(QTeamMember.teamMember.age.between(10, 30))
            teamMember.username.eq("member1"),
            teamMember.age.eq(10)
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
        .selectFrom(teamMember)
        .fetchResults();
    results.getTotal();
    List<TeamMember> content = results.getResults();

    long total = queryFactory
        .selectFrom(teamMember)
        .fetchCount();
  }

  /**
   * 회원 정렬 1. 회원 나이 내림차순 2. 회원 이름 올림차순 단, 회원 이름 없을 시, 마지막에 출력(null last)
   */
  @Test
  public void sort() {
    em.persist(new TeamMember(null, 100));
    em.persist(new TeamMember("member5", 100));
    em.persist(new TeamMember("member6", 100));

    List<TeamMember> result = queryFactory
        .selectFrom(teamMember)
        .where(teamMember.age.eq(100))
        .orderBy(
            teamMember.age.desc(),
            teamMember.username.asc().nullsLast()
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
        .selectFrom(teamMember)
        .orderBy(teamMember.username.desc())
        .offset(1)
        .limit(2)
        .fetch();
    Assertions.assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void paging2() {
    QueryResults<TeamMember> queryResults = queryFactory
        .selectFrom(teamMember)
        .orderBy(teamMember.username.desc())
        .offset(1)
        .limit(2)
        .fetchResults();
    Assertions.assertThat(queryResults.getTotal()).isEqualTo(4);
    Assertions.assertThat(queryResults.getLimit()).isEqualTo(2);
    Assertions.assertThat(queryResults.getOffset()).isEqualTo(1);
    Assertions.assertThat(queryResults.getResults()).isEqualTo(2);
  }

  @Test
  public void aggregation() {
    List<Tuple> result = queryFactory
        .select(
            teamMember.count(),
            teamMember.age.sum(),
            teamMember.age.avg(),
            teamMember.age.max(),
            teamMember.age.min()
        )
        .from(teamMember)
        .fetch();

    Tuple tuple = result.get(0);
    Assertions.assertThat(tuple.get(teamMember.count())).isEqualTo(4);
    Assertions.assertThat(tuple.get(teamMember.age.sum())).isEqualTo(100);
    Assertions.assertThat(tuple.get(teamMember.age.avg())).isEqualTo(25);
    Assertions.assertThat(tuple.get(teamMember.age.max())).isEqualTo(40);
    Assertions.assertThat(tuple.get(teamMember.age.max())).isEqualTo(10);
  }

  /**
   * 팀의 이름과 각 팀의 평균 연령을 구해라.
   */
  @Test
  public void group() throws Exception {
    List<Tuple> result = queryFactory
        .select(team.name, teamMember.age.avg())
        .from(teamMember)
        .join(teamMember.team, team)
        .groupBy(team.name)
        .fetch();

    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);

    Assertions.assertThat(teamA.get(team.name)).isEqualTo("teamA");
    Assertions.assertThat(teamA.get(teamMember.age.avg())).isEqualTo(15);

    Assertions.assertThat(teamB.get(team.name)).isEqualTo("teamB");
    Assertions.assertThat(teamB.get(teamMember.age.avg())).isEqualTo(35);
  }

  /**
   * 팀 A 소속 모든 회원
   */
  @Test
  public void join() {
    List<TeamMember> result = queryFactory
        .selectFrom(teamMember)
        .join(teamMember.team, team)
        .where(team.name.eq("teamA"))
        .fetch();
    Assertions.assertThat(result)
        .extracting("username")
        .containsExactly("member1", "member3");
  }

  /**
   * 연관관계가 없어도 join 하는 예시
   */
  @Test
  public void theta_join() {
    em.persist(new TeamMember("teamA"));
    em.persist(new TeamMember("teamB"));

    List<TeamMember> result = queryFactory
        .select(teamMember)
        .from(teamMember, team)
        .where(teamMember.username.eq(team.name))
        .fetch();

    Assertions.assertThat(result)
        .extracting("username")
        .containsExactly("teamA", "teamB");
  }

  /**
   * 회원과 팀을 join하면서, 팀 명이 teamA인 팀만 join하고 회원을 모두 조회한다. select m, t from TeamMember tm left join
   * tm.team t on t.name = 'teamA'
   */
  @Test
  public void join_on_filtering() {

    //1) 동일 결과
//    List<Tuple> result = queryFactory
//        .select(teamMember, team)
//        .from(teamMember)
//        .leftJoin(teamMember.team, team)
//        .on(team.name.eq("teamA"))
//        .fetch();

    //2) 동일 결과
    List<Tuple> result = queryFactory
        .select(teamMember, team)
        .from(teamMember)
        .join(teamMember.team, team)
        .where(team.name.eq("teamA"))
        .fetch();

    for (Tuple tuple : result) {
      System.out.println(tuple);
    }
  }

  /**
   * 연관관계 없는 엔티티 외부 join 회원 이름이 팀 이름과 같은 대상 외부 join
   */
  @Test
  public void join_on_no_relation() {
    em.persist(new TeamMember("teamA"));
    em.persist(new TeamMember("teamB"));
    em.persist(new TeamMember("teamC"));

    List<TeamMember> result = queryFactory
        .select(teamMember)
        .from(teamMember)
        .leftJoin(team).on(teamMember.username.eq(team.name))
        .fetch();

    Assertions.assertThat(result)
        .extracting("username")
        .containsExactly("teamA", "teamB");
  }

  @PersistenceUnit
  EntityManagerFactory emf;

  @Test
  public void fetchJoinNo() {
    em.flush();
    em.clear();

    TeamMember findTeamMember = queryFactory
        .selectFrom(teamMember)
        .join(teamMember.team, team).fetchJoin()
        .where(teamMember.username.eq("member1"))
        .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findTeamMember.getTeam());
    Assertions.assertThat(loaded).as("패치 조인 미적용").isTrue();
  }

  /**
   * 나이가 가장 많은 회원 조회
   */
  @Test
  public void subQuery() {
    QTeamMember teamMemberSub = new QTeamMember("teamMemberSub");
    List<TeamMember> result = queryFactory
        .selectFrom(teamMember)
        .where(teamMember.age.eq(
            JPAExpressions
                .select(teamMemberSub.age.max())
                .from(teamMemberSub)
        ))
        .fetch();
    Assertions.assertThat(result).extracting("age")
        .containsExactly(40);
  }

  /**
   * 나이가 평균 이상 회원
   */
  @Test
  public void subQueryGeo() {
    QTeamMember teamMemberSub = new QTeamMember("teamMemberSub");
    List<TeamMember> result = queryFactory
        .selectFrom(teamMember)
        .where(teamMember.age.goe(
            JPAExpressions
                .select(teamMemberSub.age.avg())
                .from(teamMemberSub)
        ))
        .fetch();

    Assertions.assertThat(result).extracting("age")
        .containsExactly(30, 40);
  }

  /**
   * 나이가 평균 이상 회원
   */
  @Test
  public void subQueryIn() {
    QTeamMember teamMemberSub = new QTeamMember("teamMemberSub");
    List<TeamMember> result = queryFactory
        .selectFrom(teamMember)
        .where(teamMember.age.in(
            JPAExpressions
                .select(teamMemberSub.age)
                .from(teamMemberSub)
                .where(teamMemberSub.age.gt(10))
        ))
        .fetch();
    Assertions.assertThat(result).extracting("age")
        .containsExactly(20, 30, 40);
  }

  @Test
  public void selectSubQuery() {
    QTeamMember teamMemberSub = new QTeamMember("teamMemberSub");
    List<Tuple> result = queryFactory
        .select(teamMember.username,
            JPAExpressions
                .select(teamMemberSub.age.avg())
                .from(teamMemberSub))
        .from(teamMember)
        .fetch();
    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  @Test
  public void basicCase() {
    List<String> result = queryFactory
        .select(teamMember.age
            .when(10).then("스무살")
            .when(20).then("스무살")
            .otherwise("기타"))
        .fetch();
    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  public void complexCase() {
    List<String> result = queryFactory
        .select(new CaseBuilder()
            .when(teamMember.age.between(0, 20)).then("0~20살")
            .when(teamMember.age.between(21, 30)).then("21~30살")
            .otherwise("기타")
        ).from(teamMember)
        .fetch();
    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  public void constant() {
    List<Tuple> result = queryFactory
        .select(teamMember.username, Expressions.constant("A"))
        .from(teamMember)
        .fetch();
    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  @Test
  public void concat() {
    //{username}_{age}
    List<String> result = queryFactory
        .select(teamMember.username.concat("_").concat(teamMember.age.stringValue()))
        .from(teamMember)
        .where(teamMember.username.eq("member1"))
        .fetch();
    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  public void simpleProjection() {
    List<String> result = queryFactory
        .select(teamMember.username)
        .from(teamMember)
        .fetch();
    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  public void tupleProjection() {
    List<Tuple> result = queryFactory
        .select(teamMember.username, teamMember.age)
        .from(teamMember)
        .fetch();

    for (Tuple tuple : result) {
      String username = tuple.get(teamMember.username);
      Integer age = tuple.get(teamMember.age);
      System.out.println("username = " + username);
      System.out.println("age = " + age);
    }
  }

  @Test
  public void findDtoByField() {

    List<TeamMemberDto> result = queryFactory
        .select(Projections.bean(TeamMemberDto.class,
            teamMember.username,
            teamMember.age))
        .from(teamMember)
        .fetch();

    for (TeamMemberDto teamMemberDto : result) {
      System.out.println("teamMemberDto = " + teamMemberDto);
    }
  }

  @Test
  public void findUserDto() {
    QTeamMember teamMemberSub = new QTeamMember("teamMemberSub");
    List<UserDto> result = queryFactory
        .select(Projections.fields(UserDto.class,
            teamMember.username.as("name"),
            ExpressionUtils.as(JPAExpressions
                .select(teamMemberSub.age.max())
                .from(teamMemberSub), "age")
            ))
        .from(teamMember)
        .fetch();

    for (UserDto userDto : result) {
      System.out.println("userDto = " + userDto);
    }
  }

  @Test
  public void findDtoByConstructor() {
    List<UserDto> result = queryFactory
        .select(Projections.constructor(UserDto.class,
            teamMember.username,
            teamMember.age))
        .from(teamMember)
        .fetch();
    for (UserDto teamMemberDto : result) {
      System.out.println("teamMemberDto = " + teamMemberDto);
    }
  }

  @Test
  public void findDtoByQueryProjection() {
    List<TeamMemberDto> result = queryFactory
        .select(new QTeamMemberDto(teamMember.username, teamMember.age))
        .from(teamMember)
        .fetch();
    for (TeamMemberDto teamMemberDto : result) {
      System.out.println("teamMemberDto = " + teamMemberDto);
    }
  }

  @Test
  public void dynamicQuery_BooleanBuilder() {
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<TeamMember> result = searchTeamMember1(usernameParam, ageParam);
    Assertions.assertThat(result.size()).isEqualTo(1);

  }

  private List<TeamMember> searchTeamMember1(String usernameCond, Integer ageCond) {
    BooleanBuilder builder = new BooleanBuilder();
    if (usernameCond != null) {
      builder.and(teamMember.username.eq(usernameCond));
    }
    if (ageCond != null) {
      builder.and(teamMember.age.eq(ageCond));
    }
    return queryFactory
        .selectFrom(teamMember)
        .where(builder)
        .fetch();
  }

  private List<TeamMember> searchTeamMember2(String usernameCond, Integer ageCond) {
    return queryFactory
        .selectFrom(teamMember)
        //.where(usernameEq(usernameCond), ageEq(ageCond))
        .where(allEq(usernameCond, ageCond))
        .fetch();
  }

  private BooleanExpression usernameEq(String usernameCond) {
    return usernameCond == null ? null : teamMember.username.eq(usernameCond);
  }

  private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? teamMember.age.eq(ageCond) : null;
  }

  private BooleanExpression allEq(String usernameCond, Integer ageCond) {
    return usernameEq(usernameCond).and(ageEq(ageCond));
  }

  @Test
  @Commit
  public void bulkUpdate() {

    //member 1 = 10 -> 비회원
    //member 2 = 20 -> 비회원

    long count = queryFactory
        .update(teamMember)
        .set(teamMember.username, "비회원")
        .where(teamMember.age.lt(28))
        .execute();
    em.flush();
    em.clear();
  }

  @Test
  public void buldAdd() {
    long count = queryFactory
        .update(teamMember)
//        .set(teamMember.age, teamMember.age.add(1))
        .set(teamMember.age, teamMember.age.multiply(2))
        .execute();
  }

  @Test
  public void bulkDelete() {
    long count = queryFactory
        .delete(teamMember)
        .where(teamMember.age.gt(18))
        .execute();
  }

  @Test
  public void sqlFunction() {
    List<String> result = queryFactory
        .select(Expressions.stringTemplate(
            "function('replace', {0}, {1}, {2})",
            teamMember.username, "member", "M"
        ))
        .from(teamMember)
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  public void sqlFunction2() {
    List<String> result = queryFactory
        .select(teamMember.username)
        .from(teamMember)
//        .where(teamMember.username.eq(Expressions.stringTemplate(
//            "function('lower', {0})",
//            teamMember.username
//        )))
        .where(teamMember.username.eq(teamMember.username.lower()))
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

}
