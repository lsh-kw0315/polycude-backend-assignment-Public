package prac.backendassingment.domain.model;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Product {
    private Long id;
    private String name;
    private Long price;
    private Long totalAmount;

    public Product(String name, Long price, Long totalAmount){
        this(null, name, price, totalAmount);
    }

    public Product(Long id, String name, Long price, Long totalAmount){
        this.id = id;

        if(name == null || name.isEmpty()) throw new IllegalArgumentException("상품의 이름은 누락되어선 안됩니다.");
        this.name = name;

        if(price == null || price < 0) throw new IllegalArgumentException("상품의 가격은 0보다 작을 수는 없습니다.");
        this.price = price;

        if(totalAmount == null || totalAmount < 0) throw new IllegalArgumentException("상품의 갯수를 0개보다 적게 등록할 수는 없습니다.");
        this.totalAmount = totalAmount;
    }

    public void increaseAmount(Long amount){
        if(amount <= 0) throw new IllegalArgumentException("0개 이하의 재고를 채울 수는 없습니다.");
        totalAmount += amount;
    }

    public void decreaseAmount(Long amount){
        if(amount <= 0) throw new IllegalArgumentException("0개 이하의 재고를 꺼낼 수는 없습니다..");
        if(totalAmount - amount < 0) throw new IllegalArgumentException("그 수만큼 꺼낼 수는 없습니다.");
        totalAmount -= amount;
    }
}
