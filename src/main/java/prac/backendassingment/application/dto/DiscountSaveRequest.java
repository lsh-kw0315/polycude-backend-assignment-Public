package prac.backendassingment.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.domain.model.DiscountCondition;
import prac.backendassingment.domain.model.DiscountPolicy;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscountSaveRequest {
    private String name;
    private DiscountPolicy discountPolicy;
    private List<DiscountCondition> conditions;
}
