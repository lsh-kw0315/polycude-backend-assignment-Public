package prac.backendassingment.domain.model;

import lombok.*;
import prac.backendassingment.global.enums.PaymentMethod;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Payment {
    private Long id;
    private Long orderId;
    private PaymentMethod paymentMethod;
    private Long finalPrice;
    private LocalDateTime createdAt;

    public Payment(Long orderId, PaymentMethod paymentMethod, Long finalPrice){
        this(null, orderId, paymentMethod, finalPrice, null);
    }

    public Payment(Long id, Long orderId, PaymentMethod paymentMethod, Long finalPrice, LocalDateTime createdAt){
        this.id = id;

        if(orderId == null || orderId <=0) throw new IllegalArgumentException("결제할 주문이 누락되어서는 안됩니다.");
        this.orderId = orderId;

        if(paymentMethod == null) throw new IllegalArgumentException("결제 수단은 누락되어서는 안됩니다.");
        this.paymentMethod = paymentMethod;

        if(finalPrice == null || finalPrice < 0) throw new IllegalArgumentException("결제 금액은 0보다 작아서는 안됩니다.");
        this.finalPrice = finalPrice;

        this.createdAt = createdAt;
    }
}
