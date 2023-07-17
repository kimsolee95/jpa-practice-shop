package com.example.demo.controller;

import com.example.demo.domain.Address;
import com.example.demo.domain.Member;
import com.example.demo.service.MemberService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/members/new")
  public ResponseEntity<String> create(@Valid MemberForm form, BindingResult result) {

    if (result.hasErrors()) {
      log.error("result: ", result);
    }

    Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
    Member member = new Member();
    member.setName(form.getName());
    member.setAddress(address);
    memberService.join(member);
    return new ResponseEntity<>(member.getName(), HttpStatus.OK);
  }

  @GetMapping("/members")
  public ResponseEntity<?> list() {
    List<Member> members = memberService.findMembers();
    return new ResponseEntity<>(members, HttpStatus.OK);
  }

}
