# Section 1. 프로젝트 환경설정

------

## 프로젝트 생성

- dependencies
  - lombok
  - spring-boot-starter-data-jpa
  - spring-boot-starter-thymeleaf
  - spring-boot-starter-web
  - h2
- 핵심 라이브러리
  - 스프링 MVC
  - 스프링 ORM
  - JPA, 하이버네이트
  - 스프링 데이터 JPA
- 기타 라이브러리
  - H2 데이터베이스 클라이언트
  - 커넥션 풀: 부트 기본은 HikariCP
  - WEB(thymeleaf)
  - 로깅 SLF4j & Logback
  - 테스트

> 참고: 스프링 데이터 JPA 는 스프링과 JPA를 먼저 이해하고 사용해야 하는 응용기술이다.



## h2 Database 구축

- `~/dev/h2/h2/bin/h2.sh` 실행 ⇒ 터미널에서 실행하면 자바로 h2 애플리케이션이 하나 프로세스로 뜬다.
- 데이터베이스 파일 생성 방법
  - `[]()` 접속
  - `/Users/sukholim/dev/h2` 아래에 data.mv.db 파일 생성 확인
  - 이후부터는 `jdbc:h2:htcp://localhost/Users/sukholim/dev/h2/data` 로 접속



### JUnit 테스트 관련

```java
package jpabook.jpashop.repository;

import jpabook.jpashop.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");

        // when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    }

}
```

- 테스트 클래스에서 DB와 관련된 테스트를 진행하려면 항상 `@Transactional` 어노테이션 작성 필요 ⇒ 테스트 했던 데이터들에 대해 다시 롤백시키기 위함.
- `@Rollback(false)` 어노테이션을 붙이면 트랜잭션 어노테이션 통해서 롤백 처리된 것을 방지하고 그대로 테스트 케이스가 DB에 반영이 된다.

> 참고: 스프링 부트를 통해 복잡한 설정이 다 자동화 되었다. `persistence.xml` 도 없고, `LocalContainerEntityManagerFactoryBean` 도 없다. 스프링 부트를 통한 추가 설정은 스프링 부트 메뉴얼을 참고하면 된다.



### 확인

- 터미널 상에서 `./gradelw clean build` 를 통해 jar 파일로 빌드하고 `java -jar` 로 실행되는지까지 확인



### 쿼리 파라미터 로그 남기기

- 로그에 다음을 추가하기 `org.hibernate.type` : SQL 실행 파라미터를 로그로 남긴다.
- 외부 라이브러리는 p6spy 쓰면 좋을듯?