package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // spring container 안에서 테스트를 돌리게해줌
@Transactional // rollback
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    // live template - tdd
    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long savedId = memberService.join(member);

        // then
        // Assertions.assertEquals(savedId, memberRepository.find(savedId).getId());
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");
        // when
        Long savedId1 = memberService.join(member1);

        assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));
//        try{
//            Long savedId2 = memberService.join(member2); // 예외가 발생하여 나가야함
//        } catch (IllegalStateException e) {
//            return;
//        }

        // then
        // Assertions.fail()
        // fail("예외가 발생해야 한다.");
    }


}