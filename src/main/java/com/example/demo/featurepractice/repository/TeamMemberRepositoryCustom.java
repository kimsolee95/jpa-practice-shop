package com.example.demo.featurepractice.repository;

import com.example.demo.featurepractice.dto.MemberSearchCondition;
import com.example.demo.featurepractice.dto.MemberTeamDto;
import java.util.List;

public interface TeamMemberRepositoryCustom {

  List<MemberTeamDto> search(MemberSearchCondition condition);

}
