# 섹션 4. 회원 도메인 개발

---

### 구현 기능

- 회원 등록
- 회원 목록 조회



### 순서

1. 회원 엔티티 코드 다시 보기
2. 회원 리포지토리 개발
3. 회원 서비스 개발
4. 회원 기능 테스트



### 회원 리포지토리 개발

``` java
package jpabook.jpashop.repository;

import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList(); // entity 대상으로 쿼리를 한다고 생각하면 됨.
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}

```

- 기술 설명
  - `@Repository` : 스프링 빈으로 등록, JPA 예외를 스프링 기반 예외로 예외 변환
  - `@PersistenceContext` : 엔티티 매니저(`EntityManager`) 주입
  - `@PersistenceUnit` : 엔티티 매니저 팩토리(`EntityManagerFactory`) 주입 (엔티티 매니저 팩토리를 직접 주입하고자 할 때 사용한다)
- 기능 설명
  - `save()`: 영속성 컨텍스트에 일단 member 객체를 넣은 다음에 나중에 트랜잭션이 커밋 되는 시점에 DB에 반영이 된다.
  - `findOne()`: 단건 조회
  - `findAll()`
    - JPQL: 엔티티 객체를 대상으로 쿼리를 한다고 생각하면 됨 (SQL은 테이블을 대상으로 쿼리를 하는 것)
  - `findByName()`



### 회원 서비스 개발

``` java
package jpabook.jpashop.service;

import java.util.List;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional // 데이터를 읽는 로직이 아닌 메소드에는 readOnly = true 값을 넣어주면 안된다. => 데이터를 읽기만하고 넣지를 못함
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 단건 조회
     */
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
```

- 기술 설명
  - `@Service`
  - `@Transcational`: 트랜잭션, 영속성 컨텍스트
    - JPA 영역에서의 모든 데이터 변경은 Transaction 영역 안에서 실행되어야 한다
    - `readOnly=true`: 데이터의 변이 없는 읽기 전용 메서드에 사용, 영속성 컨텍스트를 플러시 하지 않으므로 약간의 성능 향상(읽기 전용에는 다 적용)
    - 데이터베이스 드라이버가 지원하면 DB에서 성능 향상
  - `@Autowired`
    - 생성자 injection 많이 사용, 생성자가 하나면 생략 가능
    - 단점이 많다 => 일단 못바꿈 (테스트 등등시에... 주입이 어렵다. mock 객체를 쓸 때 주입이 어렵다)
    - 롬복의 `@RequiredArgsConstructor` 를 사용해서 각 객체를 final 로 선언해서 쓰는 방식이 더 좋다 => @RequiredArgsConstructor 는 final 이 붙은 객체들만 파라미터로 가진 생성자를 만들어준다.
    - 참고로 MemberRepository 에서의 EntityManager 도 spring boot 에서는 @Autowired 로 빈 주입이 가능하기 때문에 `@RequiredArgsConstructor` 로 EntityManager 에 생성자 주입이 가능하다.
- 기능 설명
  - `join()`
  - `findMembers()`
  - `findOne()`

> 참고: 실무에서는 검증 로직이 있어도 멀티 쓰레드 상황을 고려해서 회원 테이블의 회원명 컬럼에 유니크 제약 조건을 추가하는 것이 안전한다.
> 참고: 스프링 필드 주입 대신에 생성자 주입을 사용하자.



### 회원 기능 테스트

- 테스트 요구사항
  - 회원가입을 성공해야 한다.
  - 회원가입 할 때 같은 이름이 있으면 예외가 발생해야 한다.

``` java
package jpabook.jpashop.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("kim");
        // when
        Long saveId = memberService.join(member);
        // then
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        memberService.join(member2); // 예외가 발생해야 한다.

        // then
        fail("예외가 발생해야 한다");
    }
}
```

- `회원가입()` 을 호출하고 콘솔에 찍힌 로그를 확인해보면 실제 INSERT 문이 없다. 왜냐하면 트랜잭션이 커밋이 될 때 flush를 하면서 DB에 insert 쿼리가 나가게 되기 때문이다.

- `@Transactional` 어노테이션이 기본적으로 테스트 케이스 위에 붙어 있으면 기본적으로 rollback 처리를 한다. 롤백을 안하려면 `@Rollback(value = false)` 를 붙여주면 된다.

  - 롤백을 하는 이유는 테스트는 반복적으로 해야하기 때문에 DB를 비워줘야 하는 것이다.

- 부분적으로 트랜잭션을 커밋을 하려면 EntityManager 를 빈 주입을 받아서 강제로 flush() 를 호출해주면 된다. (아래 예시 참고)

  ``` java
  public class MemberServiceTest {
    ...
    @Autowired
    EntityManager em;
    
    public void 회원가입() throws Exception {
      Member member = new Member();
      member.setName("kim");
      Long savedId = memberService.join(member);
      em.flush(); // 여기!
    }
  }
  ```

- `Assert.fail` 은 오면 안되는 부분까지 호출될 때 실패로 떨궈야 할 때 쓰는 메소드다.

- 기술 설명
  - `@RunWith(SpringRunner.class)`: 스프링과 테스트 통합
  - `@SpringBootTest`: 스프링 부트 띄우고 테스트(이게 없으면 Autowired가 다 실패한다)
  - `@Transactional`: 반복 가능한 테스트 지원, 각각의 테스트를 실행할 때마다 트랜잭션을 시작하고 테스트가 끝나면 트랜잭션을 강제로 롤백 (이 어노테이션이 테스트 케이스에서 사용될 떄만 롤백)



### 테스트 케이스를 위한 설정

테스트 케이스는 격리된 환경에서 실행하고, 끝나면 데이터를 초기화하는 것이 좋다. 그런 면에서 메모리 DB를 사용하는 것이 가장 이상적이다.

추가로 테스트 케이스를 위한 스프링 환경과, 일반적으로 애플리케이션을 실행하는 환경은 보통 다르므로 설정 파일을 다르게 사용하자.

다음과 같이 간단하게 테스트용 설정 파일을 추가하면 된다.

- `test/resources/application.yml` => 테스트 쪽 디렉토리 안에서 실행되는 테스트용 코드들은 설정 파일을 가장 먼저 test/resources 아래에서 읽는다.

  ``` yaml
  spring:
    datasource:
      url: jdbc:h2:mem:testdb # in-memory 모드의 h2 데이터베이스 사용
      username: sa
      password:
      driver-class-name: org.h2.Driver
    jpa:
      hibernate:
        ddl-auto: create
      properties:
        hibernate:
          show_sql: true
          format_sql: true
        open-in-view: false
  
  logging.level:
    org.hibernate.SQL: debug
  ```

- 하지만 spring boot 에서는 위와 같은 기본 설정이 없으면 기본적으로 메모리 모드로 돌려버린다.

- 스프링 부트는 datasource 설정이 없으면, 기본적으로 메모리 DB를 사용하고, driver-class도 현재 등록된 라이브러리를 보고 찾아준다. 추가로 `ddl-auto` 도 `create-drop` 모드로 동작한다. 따라서 데이터 소스나, JPA 관련된 별도의 추가 설정을 하지 않아도 된다.





















