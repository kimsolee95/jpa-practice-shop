package com.example.demo.featurepractice;

import com.example.demo.featurepractice.entity.Member;
import com.example.demo.featurepractice.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Commit
public class MemberTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void testEntity() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamB);
    Member member3 = new Member("member3", 10, teamA);
    Member member4 = new Member("member4", 20, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    //초기화
    em.flush();
    em.clear();
    //확인
    List<Member> members = em.createQuery("select m from Member m", Member.class)
        .getResultList();
  }

}
