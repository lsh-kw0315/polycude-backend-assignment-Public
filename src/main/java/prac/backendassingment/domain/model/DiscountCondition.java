package prac.backendassingment.domain.model;

import lombok.*;
import prac.backendassingment.global.enums.DiscountReason;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class DiscountCondition {
    private DiscountReason discountReason;
    private String condition;

    public DiscountCondition(DiscountReason discountReason, String condition){
        if(discountReason == null) throw new IllegalArgumentException("할인 이유 항목은 필수입니다.");
        this.discountReason = discountReason;

        if(condition == null || condition.isEmpty()) throw new IllegalArgumentException("이유 항목에 대응하는 값(Condition)이 없습니다.");
        this.condition = condition;
    }
}
