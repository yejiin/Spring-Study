package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 회원 등록
 * 회원 조회
 */
@Repository
@RequiredArgsConstructor  // Spring Data JPA 를 사용하면 EntityManager 도 주입가능
public class MemberRepositoryOld {

//    @PersistenceContext  //EntityManager 주입
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);  //(타입, pk)
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)  //JPQL 작성 (SQL과 기능은 같지만 문법이 조금 다름)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }


}
