package jpabook.jpashop.service;

import jpabook.jpashop.entity.Delivery;
import jpabook.jpashop.entity.Member;
import jpabook.jpashop.entity.Order;
import jpabook.jpashop.entity.OrderItem;
import jpabook.jpashop.entity.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        //이 로직에서 딜리버리, 오더아이템 들을 따로 저장 안 하는 이유는 cascadeType.ALL 때문
        //cascade 는 완전 종속적인 경우에만 사용. ex) delivery, OrderItem 은 order 에만 의존

        //엔티티 조회
        Member member = memberRepository.findById(memberId).get();
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
    }

    /**
     * 주문 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }
}

