package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  //주문 회원

    //(fetch = FetchType.EAGER)
    //JPQL select o from order o; -> SQL select * from order n + 1(order)

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    /*
    persist(orderItemA)
    persist(orderItemB)
    persist(orderItemC)
    persist(order)

    cascade 적용시
    persist(order)
    */

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;  //배송정보

    private LocalDateTime orderDate;  //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status;  //주문상태 (ORDER, CANCEL)

    //==연관관게 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

//    public static void main(String[] args) {
//        Member member = new Member();
//        Order order = new Order();
//
//        member.getOrders().add(order);//
//        order.setMember(member);
//    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    //주문 엔티티를 생성할 때 사용
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /** 주문 취소 */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);  //this 사용은 개인스타일에 따라
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;

//        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();   //JAVA8
    }
}
