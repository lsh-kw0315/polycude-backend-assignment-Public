package prac.backendassingment.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.domain.model.DiscountCondition;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscountSearchRequest {
    private List<DiscountCondition> conditions;
}
