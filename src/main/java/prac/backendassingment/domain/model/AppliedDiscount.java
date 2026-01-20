package prac.backendassingment.domain.model;

import lombok.*;
import prac.backendassingment.global.enums.DiscountMethod;
import prac.backendassingment.global.enums.DiscountReason;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
@ToString
public class AppliedDiscount {

    private String name;
    private DiscountPolicy discountPolicy;
    private List<DiscountCondition> conditions;
    private Long discountedPrice;


    public AppliedDiscount(Discount discount, Long discountedPrice){
        if(discount == null) throw new IllegalArgumentException("할인 정책은 존재해야합니다.");
        this.name = discount.getName();
        this.discountPolicy = discount.getDiscountPolicy();
        this.conditions = discount.getConditions();

        this.discountedPrice = discountedPrice != null ? discountedPrice : 0L;
    }
}
