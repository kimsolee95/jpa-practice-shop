package com.example.demo.featurepractice.repository;

import static com.example.demo.featurepractice.entity.QTeam.team;
import static com.example.demo.featurepractice.entity.QTeamMember.teamMember;

import com.example.demo.featurepractice.dto.MemberSearchCondition;
import com.example.demo.featurepractice.dto.MemberTeamDto;
import com.example.demo.featurepractice.dto.QMemberTeamDto;
import com.example.demo.featurepractice.entity.TeamMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class TeamMemberJpaRepository {

  private final EntityManager em;
  private final JPAQueryFactory queryFactory;

  public TeamMemberJpaRepository(EntityManager em) {
    this.em = em;
    this.queryFactory = new JPAQueryFactory(em);
  }

  public void save(TeamMember teamMember) {
    em.persist(teamMember);
  }

  public Optional<TeamMember> findById(Long id) {
    TeamMember findTeamMember = em.find(TeamMember.class, id);
    return Optional.ofNullable(findTeamMember);
  }

  public List<TeamMember> findAll() {
    return em.createQuery("select tm from TeamMember tm", TeamMember.class)
        .getResultList();
  }

  public List<TeamMember> findAll_Querydsl() {
    return queryFactory
        .selectFrom(teamMember)
        .fetch();
  }

  public List<TeamMember> findByUsername(String username) {
    return em.createQuery("select tm from TeamMember tm where tm.username = :username", TeamMember.class)
        .setParameter("username", username)
        .getResultList();
  }

  public List<TeamMember> findByUsername_Querydsl(String username) {
    return queryFactory
        .selectFrom(teamMember)
        .where(teamMember.username.eq(username))
        .fetch();
  }

  /**
   * 동적 쿼리 : booleanBuilder
   * */
  public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition) {

    BooleanBuilder builder = new BooleanBuilder();
    if (StringUtils.hasText(condition.getUsername())) {
      builder.and(teamMember.username.eq(condition.getUsername()));
    }
    if (StringUtils.hasText(condition.getTeamName())) {
      builder.and(team.name.eq(condition.getTeamName()));
    }
    if (condition.getAgeGoe() != null) {
      builder.and(teamMember.age.goe(condition.getAgeGoe()));
    }
    if (condition.getAgeLoe() != null) {
      builder.and(teamMember.age.goe(condition.getAgeGoe()));
    }

    return queryFactory
        .select(new QMemberTeamDto(
                teamMember.id.as("memberId"),
                teamMember.username,
                teamMember.age,
                team.id.as("teamId"),
                team.name.as("teamName")))
        .from(teamMember)
        .leftJoin(teamMember.team, team)
        .where(builder)
        .fetch();
  }

  /**
   * 동적 쿼리 : BooleanExpression
   * */
  public List<MemberTeamDto> searchTeamMember(MemberSearchCondition condition) {
    return queryFactory
        .select(new QMemberTeamDto(
            teamMember.id.as("memberId"),
            teamMember.username,
            teamMember.age,
            team.id.as("teamId"),
            team.name.as("teamName")))
        .from(teamMember)
        .leftJoin(teamMember.team, team)
        .where(
            usernameEq(condition.getUsername()),
            teamNameEq(condition.getTeamName()),
            ageGoe(condition.getAgeGoe()),
            ageLoe(condition.getAgeLoe())
            //ageBetween(condition.getAgeLoe(), condition.getAgeGoe())
            )
        .fetch();
  }

  private BooleanExpression ageBetween(int ageLoe, int ageGoe) {
    return ageGoe(ageLoe).and(ageGoe(ageGoe));
  }

  private BooleanExpression usernameEq(String username) {
    return StringUtils.hasText(username) ? teamMember.username.eq(username) : null;
  }

  private BooleanExpression teamNameEq(String teamName) {
    return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
  }

  private BooleanExpression ageGoe(Integer ageGoe) {
    return  ageGoe != null ? teamMember.age.goe(ageGoe) : null;
  }

  private BooleanExpression ageLoe(Integer ageLoe) {
    return ageLoe != null ? teamMember.age.loe(ageLoe) : null;
  }

}
