package prac.backendassingment.infra.persistence.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.global.enums.DiscountReason;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 필수: 값 객체 비교를 위해
public class DiscountConditionValue {
    @Enumerated(EnumType.STRING)
    private DiscountReason discountReason;

    private String condition;
}
