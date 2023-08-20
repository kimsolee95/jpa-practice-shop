package com.example.demo.featurepractice.controller;

import com.example.demo.featurepractice.dto.MemberSearchCondition;
import com.example.demo.featurepractice.dto.MemberTeamDto;
import com.example.demo.featurepractice.repository.TeamMemberJpaRepository;
import com.example.demo.featurepractice.repository.TeamMemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamMemberController {

  private final TeamMemberJpaRepository teamMemberJpaRepository;
  private final TeamMemberRepository teamMemberRepository;

  @GetMapping("/v1/members")
  public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
    return teamMemberJpaRepository.searchTeamMember(condition);
  }

  @GetMapping("/v2/members")
  public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
    return teamMemberRepository.searchPageSimple(condition, pageable);
  }

  @GetMapping("/v3/members")
  public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
    return teamMemberRepository.searchPageComplex(condition, pageable);
  }
}
