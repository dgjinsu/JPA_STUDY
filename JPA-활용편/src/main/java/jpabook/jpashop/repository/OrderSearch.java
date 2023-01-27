package jpabook.jpashop.repository;

import jpabook.jpashop.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderSearch {
    private String memberName; //회원 이름
    private OrderStatus orderStatus; //주문상태(ORDER, CANCEL)

}
