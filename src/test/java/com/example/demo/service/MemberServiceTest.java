package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
 public class MemberServiceTest {

  @Autowired MemberService memberService;
  @Autowired MemberRepository memberRepository;

  @Test
  public void member_join() throws Exception {
    //given
    Member member = new Member();
    member.setName("kim");
    //when
    Long saveId = memberService.join(member);
    //then
    assertEquals(member, memberRepository.findOne(saveId));
  }

  @Test
  public void duplicate_member_exception() throws Exception {
    //given

    //when

    //then
  }

}