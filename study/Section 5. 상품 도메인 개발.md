# 섹션 5. 상품 도메인 개발

### 구현 기능

- 상품 등록
- 상품 목록 조회
- 상품 수정



### 순서

- 상품 엔티티 개발(비즈니스 로직 추가)
- 상품 리포지토리 개발
- 상품 서비스 개발
- 상품 기능 테스트 (단순하기 때문에 생략)



### 상품 엔티티 개발(비즈니스 로직 추가)

``` java
package jpabook.jpashop.domain.item;

import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import jpabook.jpashop.domain.Category;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {
  @Id
  @GeneratedValue
  @Column(name = "item_id")
  private Long id;
  
  private String name;
  private int price;
  private int stockQuantity;
  
  @ManyToMany(mappedBy = "items")
  private List<Category> categories = new ArrayList<Category>();
  
  
  //==비즈니스 로직==//
  public void addStock(int quantity) {
    this.stockQuantity += quantity;
  }
  
  public void removeStock(int quantity) {
    int restStock = this.stockQuantity - quantity;
    if (restStock < 0) {
      throw new NotEnoughStockException("need more stock");
    }
    this.stockQuantity = restStock;
  }
}
```

- 데이터를 가지고 있는 쪽에 비즈니스 로직이 있는 것이 가장 좋다 => 그래야 응집력이 있다.
  - 응집력(cohesion): 프로그램의 한 요소가 해당 기능을 수행하기 위해 얼마만큼의 연관된 책임과 아이디어가 뭉쳐있는지를 나타내는 정도. 프로그램의 한 요소가 특정 목적을 위해 밀접하게 연관된 기능들이 모여서 구현되어 있고, 지나치게 많은 일을 하지 않으면 그것을 응집력이 높다고 표현한다.



### 예외 추가

``` java
package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {
  public NotEnoughStockException() {
    
  }
  
  public NotEnoughStockException(String message) {
    super(message);
  }
  
  public NotEnoughStockException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public NotEnoughStockException(Throwable cause) {
    super(cause);
  }
}
```



### 비즈니스 로직 분석

- `addStock()` 메소드는 파라미터로 넘어온 수만큼 재고를 늘린다. 이 메소드는 재고가 증가하거나 상품 주문을 취소해서 재고를 다시 늘려야 할 떄 사용한다.
- `removeStock()` 메소드는 파라미터로 넘어온 수만큼 재고를 줄인다. 만약 재고가 부족하면 예외가 발생한다. 주로 상품을 주문할 때 사용한다.





### 상품 리포지토리 개발

``` java
package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
  
  private final EntityManager em;
  
  public void save(Item item) {
    if (item.getId() == null) { // id가 없다는 것은 완전히 새로 생성한 객체라는 의미다.
      em.persist(item);
    } else {
      em.merge(item);
    }
  }
  
  public Item findOne(Long id) {
    return em.find(Item.class, id);
  }
  
  public List<Item> findAll() {
    return em.createQuery("select i from Item i", Item.class).getResultList();
  }
}
```

- 기능 설명
  - `save()`
    - id 가 없으면 신규로 보고 `persist()` 실행
    - id 가 있으면 이미 데이터베이스에 저장된 엔티티를 수정한다고 보고, `merge()` 를 실행





### 상품 서비스 개발

``` java
package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
  
  private final ItemRepository itemRepository;
  
  @Transacitonal
  public void saveItem(Item item) {
    itemRepository.save(item);
  }
  
  public List<Item> findItems() {
    return itemRepository.findAll();
  }
  
  public Item findOne(Long itemId) {
    return itemRepository.findOne(itemId);
  }
}
```

- 상품 서비스는 상품 리포지토리에 단순히 위임만 하는 클래스

