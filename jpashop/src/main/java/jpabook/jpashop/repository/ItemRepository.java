package jpabook.jpashop.repository;

import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) { // id가 없다는 것은 완전히 새로 생성한 객체라는 의미다.
            em.persist(item);
        } else {
            em.merge(item); // update 랑 비슷한건데 뒤에서 자세히 설명하기로... (이미 DB에 등록되어 있는 값이라고 이해)
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
