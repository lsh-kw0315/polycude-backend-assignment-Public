package prac.backendassingment.domain.model;

import lombok.*;
import prac.backendassingment.global.enums.DiscountMethod;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class DiscountPolicy {
    private DiscountMethod discountMethod;
    private BigDecimal discountAmount;

    public DiscountPolicy(DiscountMethod discountMethod, BigDecimal discountAmount){
        if(discountMethod == null) throw new IllegalArgumentException("할인 방식은 필수입니다.");
        this.discountMethod = discountMethod;

        if(discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("할인 값은 어떤 방식으로든 0 이하일 수 없습니다.");
        this.discountAmount = discountAmount;
    }

    // 도메인 메서드 추가
    public BigDecimal applyDiscount(BigDecimal originalPrice) {
        if (this.discountMethod == DiscountMethod.FIXED) {
            return originalPrice.subtract(discountAmount);
        } else if (this.discountMethod == DiscountMethod.PERCENTAGE) {
            return originalPrice.multiply(discountAmount);
        }
        return originalPrice;
    }

}
