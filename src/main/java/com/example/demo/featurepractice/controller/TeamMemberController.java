package com.example.demo.featurepractice.controller;

import com.example.demo.featurepractice.dto.MemberSearchCondition;
import com.example.demo.featurepractice.dto.MemberTeamDto;
import com.example.demo.featurepractice.repository.TeamMemberJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamMemberController {

  private final TeamMemberJpaRepository teamMemberJpaRepository;

  @GetMapping("/v1/members")
  public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
    return teamMemberJpaRepository.searchTeamMember(condition);
  }

}
