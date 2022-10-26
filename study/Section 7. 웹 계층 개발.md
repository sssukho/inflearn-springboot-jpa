# 섹션 7. 웹 계층 개발

### 목차

- 홈 화면과 레이아웃
- 회원등록
- 회원 목록 조회
- 상품 등록
- 상품 목록
- 상품 수정
- 변경 감지와 병합(merge)
- 상품 주문
- 주문 목록 검색, 취소



## 홈 화면과 레이아웃

### 홈 컨트롤러 등록

``` java
package jpabook.jpashop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {
  @RequestMapping("/")
  public String home() {
    log.info("home controller");
    return "home";
  }
}
```



### 스프링부트 타임리프 기본 설정

``` yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
```

- 스프링부트 타임리프 viewName 매핑
  - `resources:templates/` + {viewName} + `.html`
  - `resources:templates/hmoe.html`

반환된 문자(`home`)와 스프링부트 설정(prefix, suffix) 정보를 사용해서 렌더링할 뷰(html)를 찾는다.



### 타임리프 템플릿 등록

``` html
<!-- home.html -->
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header">
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader"/>
    <div class="jumbotron"><h1>HELLO SHOP</h1>
        <p class="lead">회원 기능</p>
        <p>
            <a class="btn btn-lg btn-secondary" href="/members/new">회원 가입</a>
            <a class="btn btn-lg btn-secondary" href="/members">회원 목록</a></p>
        <p class="lead">상품 기능</p>
        <p>
            <a class="btn btn-lg btn-dark" href="/items/new">상품 등록</a>
            <a class="btn btn-lg btn-dark" href="/items">상품 목록</a></p>
        <p class="lead">주문 기능</p>
        <p>
            <a class="btn btn-lg btn-info" href="/order">상품 주문</a>
            <a class="btn btn-lg btn-info" href="/orders">주문 내역</a></p>
    </div>
    <div th:replace="fragments/footer :: footer"/>
</div> <!-- /container -->
</body>
</html>
 
```



### fragments/header

``` html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="header">
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-
  to-fit=no">
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="/css/bootstrap.min.css" integrity="sha384-
  ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
          crossorigin="anonymous">
    <!-- Custom styles for this template -->
    <link href="/css/jumbotron-narrow.css" rel="stylesheet">
    <title>Hello, world!</title>
</head>
```



### fragments/bodyHeader

``` html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<div class="header" th:fragment="bodyHeader">
    <ul class="nav nav-pills pull-right">
        <li><a href="/">Home</a></li>
    </ul>
    <a href="/"><h3 class="text-muted">HELLO SHOP</h3></a>
</div>
```



### fragments/footer.html

``` html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<div class="footer" th:fragment="footer">
    <p>&copy; Hello Shop V2</p>
</div>
```



> 참고: Hierachical-style layouts
>
> 예제에서는 뷰 템플릿을 최대한 간단하게 설명하려고 `header`, `footer` 같은 템플릿 파일을 반복해서 포함한다. 다음 링크의 Hierarchical-style layouts을 참고하면 이런 부분도 중복을 제거할 수 있다.
>
> https://www.thymeleaf.org/doc/articles/layouts.html



> 참고: 뷰 템플릿 변경사항을 서버 재시작 없이 즉시 반영하기
>
> 1. spring-boot-devtools 추가
> 2. html 파일 build -> Recompile



### view 리소스 등록

이쁜 디자니을 위해 부트스트랩을 사용. (https://getbootstrap.com/) => 버전 4.x 사용

- `resources/static` 하위에 `css`, `js` 추가
- `resources/static/css/jumbotron-narrow.css` 추가

- jumbotron-narrow.css 파일

  ``` css
  /* Space out content a bit */
  body {
      padding-top: 20px;
      padding-bottom: 20px;
  }
  
  /* Everything but the jumbotron gets side spacing for mobile first views */
  .header,
  .marketing,
  .footer {
      padding-left: 15px;
      padding-right: 15px;
  }
  
  /* Custom page header */
  .header {
      border-bottom: 1px solid #e5e5e5;
  }
  
  /* Make the masthead heading the same height as the navigation */
  .header h3 {
      margin-top: 0;
      margin-bottom: 0;
      line-height: 40px;
      padding-bottom: 19px;
  }
  
  /* Custom page footer */
  .footer {
      padding-top: 19px;
      color: #777;
      border-top: 1px solid #e5e5e5;
  }
  
  /* Customize container */
  @media (min-width: 768px) {
      .container {
          max-width: 730px;
      }
  }
  
  .container-narrow>hr {
      margin: 30px 0;
  }
  
  /* Main marketing message and sign up button */
  .jumbotron {
      text-align: center;
      border-bottom: 1px solid #e5e5e5;
  }
  
  .jumbotron .btn {
      font-size: 21px;
      padding: 14px 24px;
  }
  
  /* Supporting marketing content */
  .marketing {
      margin: 40px 0;
  }
  
  .marketing p+h4 {
      margin-top: 28px;
  }
  
  /* Responsive: Portrait tablets and up */
  @media screen and (min-width: 768px) {
  
      /* Remove the padding we set earlier */
      .header,
      .marketing,
      .footer {
          padding-left: 0;
          padding-right: 0;
      }
  
      /* Space out the masthead */
      .header {
          margin-bottom: 30px;
      }
  
      /* Remove the bottom border on the jumbotron for visual effect */
      .jumbotron {
          border-bottom: 0;
      }
  }
  ```



### 회원 등록

- 폼 객체를 사용해서 화면 계층과 서비스 계층을 명확하게 분리한다.

``` java
package jpabook.jpashop.web;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수 입니다")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
```

- `@NotEmpty` 는  `javax.validation.constraints` 를 import 해야만 사용할 수 있는데 스프링부트 2.3.0 버전부터는 web-starter 모듈에 validation 패키지가 제외되어서 별도로 dependency 지정이 필요하다.

- gradle 사용하는 경우

  ``` gradle
  implementation 'org.springframework.boot:spring-boot-starter-validation'
  ```

- maven 사용하는 경우

  ``` xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
  	<artifactId>spring-boot-starter-validation</artifactId>
  </dependency>
  ```



### 회원 등록 컨트롤러

``` java
package jpabook.jpashop.controller;

import javax.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.web.MemberForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping(value = "/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }
}

```

- `@Valid` : MemberForm 객체 내부에 있는 javax.validation 관련 어노테이션을 자동으로 인식해서 validation 처리를 해준다.
- `BindingResult`:



## 회원 목록 조회

### 회원 목록 컨트롤러 추가

``` java
package jpabook.jpashop.web;

@Controller
@RequiredArgsConstructor
public class MemberController {
  // 추가
  @GetMapping(value = "/members")
  public String list(Model model) {
    List<Member> members = memberService.findMembers();
    model.addAttribute("members", members);
    return "members/memberList";
  }
}
```

- 조회한 상품을 뷰에 전달하기 위해 스프링 MVC가 제공하는 모델(Model) 객체에 보관
- 실행할 뷰 이름을 반환



### 회원 목록 뷰(`templates/members/memberList.html`)

``` html
<!DOCTYPE HTML>
  <html xmlns:th="http://www.thymeleaf.org">
  <head th:replace="fragments/header :: header" />
  <body>
  <div class="container">
      <div th:replace="fragments/bodyHeader :: bodyHeader" />
      <div>
          <table class="table table-striped">
              <thead>
              <tr>
                  <th>#</th>
<th>이름</th> <th>도시</th> <th>주소</th> <th>우편번호</th>
              </tr>
              </thead>
              <tbody>
              <tr th:each="member : ${members}">
                  <td th:text="${member.id}"></td>
                  <td th:text="${member.name}"></td>
                  <td th:text="${member.address?.city}"></td>
                  <td th:text="${member.address?.street}"></td>
                  <td th:text="${member.address?.zipcode}"></td>
              </tr>
              </tbody>
        </table>
    </div>
    	<div th:replace="fragments/footer :: footer" />
    
    </div> <!-- /container -->
</body>
</html>
```

> 참고: 타임리프에서 ?를 사용하면 null 을 무시한다.

> 참고: 폼 객체 vs 엔티티 직접 사용
>
> 참고: 요구사항이 정말 단순할 때는 폼 객체(MemberForm) 없이 엔티티(Member)를 직접 등록과 수정화면에서 사용해도 된다. 하지만 화면 요구사항이 복잡해지기 시작하면, 엔티티에 화면을 처리하기 위한 기능이 점점 증가한다. 결과적으로 엔티티는 점점 화면에 종속적으로 변하고, 이렇게 화면 기능 때문에 지저분해진 엔티티는 결국 유지보수하기 어려워진다. 실무에서 엔티티는 핵심 비즈니스 로직만 가지고 있고, 화면을 위한 로직은 없어야 한다. 화면이나 API에 맞는 폼 객체나 DTO를 사용하자. 그래서 화면이나 API 요구사항을 이것들로 처리하고, 엔티티는 최대한 순수하게 유지하자.



































