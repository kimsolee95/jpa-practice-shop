package com.example.demo.featurepractice.repository;

import com.example.demo.featurepractice.entity.TeamMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long>, TeamMemberRepositoryCustom {

  List<TeamMember> findByUsername(String username);
}
