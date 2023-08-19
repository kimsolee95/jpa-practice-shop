package com.example.demo.featurepractice.repository;

import com.example.demo.featurepractice.dto.MemberSearchCondition;
import com.example.demo.featurepractice.dto.MemberTeamDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeamMemberRepositoryCustom {

  List<MemberTeamDto> search(MemberSearchCondition condition);

  Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

  Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable);
}
