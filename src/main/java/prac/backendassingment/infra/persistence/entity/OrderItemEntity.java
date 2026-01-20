package prac.backendassingment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private OrderEntity order;

    //일종의 스냅샷 개념으로 중복 저장하는 값
    private Long quantity;
    private Long pricePerProduct;
    private String productName;

    public OrderItemEntity(ProductEntity product, OrderEntity order, Long quantity, Long pricePerProduct, String productName){
        this(null, product, order, quantity, pricePerProduct, productName);
    }
}
