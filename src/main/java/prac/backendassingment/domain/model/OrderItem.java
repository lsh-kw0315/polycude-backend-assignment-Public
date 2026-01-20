package prac.backendassingment.domain.model;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class OrderItem {
    private Long id;
    private Long productId;
    private String productName;
    private Long quantity;
    private Long pricePerProduct;

    public OrderItem(Long productId, String productName, Long quantity, Long pricePerProduct){
        this(null, productId, productName, quantity, pricePerProduct);
    }

    public OrderItem(Long id, Long productId, String productName, Long quantity, Long pricePerProduct){
        this.id = id;

        if(productId == null || productId <=0) throw new IllegalArgumentException("주문 상품에 상품이 누락되어선 안됩니다.");
        this.productId = productId;

        if(productName == null || productName.isEmpty()) throw new IllegalArgumentException("주문 상품의 이름이 누락되어선 안됩니다.");
        this.productName = productName;

        if(quantity == null || quantity <= 0) throw new IllegalArgumentException("주문 상품의 수량이 0 이하일 수는 없습니다.");
        this.quantity = quantity;

        if(pricePerProduct == null || pricePerProduct < 0) throw new IllegalArgumentException("주문 상품 하나의 가격은 0보다 작을 수는 없습니다.");
        this.pricePerProduct = pricePerProduct;
    }

    public Long calculateTotalPrice(){
        return this.quantity * this.pricePerProduct;
    }

    public void addQuantity(int quantity){
        if(quantity <= 0) return;
        this.quantity += quantity;
    }

    public void subQuantity(int quantity){
        if(quantity <= 0) return;
        if(this.quantity - quantity <= 0) throw new IllegalArgumentException("0개 이하로 주문량을 낮출 수 없습니다.");
        this.quantity -= quantity;
    }
}
