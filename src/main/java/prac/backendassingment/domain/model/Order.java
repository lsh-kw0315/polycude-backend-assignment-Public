package prac.backendassingment.domain.model;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Order {
    private Long id;
    private Long memberId;
    private List<OrderItem> orderItems;
    private Long originalPrice;

    public Order(Long memberId, List<OrderItem> orderItems){
        this(null, memberId, orderItems);
    }

    public Order(Long id, Long memberId, List<OrderItem> orderItems){
        this.id = id;

        if(memberId == null || memberId <= 0) throw new IllegalArgumentException("유저 정보가 없는 주문은 만들 수 없습니다.");
        this.memberId = memberId;

        if(orderItems == null || orderItems.isEmpty()) throw new IllegalArgumentException("주문 상품이 없는 주문은 만들 수 없습니다.");
        this.orderItems = orderItems;

        this.originalPrice = orderItems.stream().mapToLong(OrderItem::calculateTotalPrice).sum();
    }

    public List<OrderItem> getOrderItems(){
        return Collections.unmodifiableList(orderItems);
    }

    public void addItem(OrderItem orderItem){
        if(orderItem == null) return;
        orderItems.add(orderItem);
        originalPrice += orderItem.calculateTotalPrice();
    }

    public void removeItem(OrderItem orderItem){
        if(orderItem == null) return;
        if(orderItems.contains(orderItem) && orderItems.size() == 1) throw new IllegalArgumentException("주문 상품이 없는 주문을 만들 수는 없습니다.");
        orderItems.remove(orderItem);
        originalPrice -= orderItem.calculateTotalPrice();
    }
}
