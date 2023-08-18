package com.example.demo.featurepractice.repository;

import static com.example.demo.featurepractice.entity.QTeam.team;
import static com.example.demo.featurepractice.entity.QTeamMember.teamMember;

import com.example.demo.featurepractice.dto.MemberSearchCondition;
import com.example.demo.featurepractice.dto.MemberTeamDto;
import com.example.demo.featurepractice.dto.QMemberTeamDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.util.StringUtils;

public class TeamMemberRepositoryImpl implements TeamMemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public TeamMemberRepositoryImpl(EntityManager em) {
    this.queryFactory  = new JPAQueryFactory(em);
  }

  @Override
  public List<MemberTeamDto> search(MemberSearchCondition condition) {
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
