package jpabook.jpashop.repository;

import jpabook.jpashop.model.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext // spring boot 가 해당 어노테이션을 보고 EntityManager를 자동 생성해서 bean 으로 등록해준다.
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
