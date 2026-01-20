package prac.backendassingment.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.global.enums.DiscountMethod;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // 필수: 값 객체 비교를 위해
public class DiscountPolicyValue {
    @Enumerated(EnumType.STRING)
    private DiscountMethod discountMethod;

    @Column(precision = 10, scale = 4)
    private BigDecimal discountAmount;
}
