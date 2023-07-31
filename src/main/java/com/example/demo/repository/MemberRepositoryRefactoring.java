package com.example.demo.repository;

import com.example.demo.domain.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepositoryRefactoring extends JpaRepository<Member, Long> {

  List<Member> findByName(String name);
}
