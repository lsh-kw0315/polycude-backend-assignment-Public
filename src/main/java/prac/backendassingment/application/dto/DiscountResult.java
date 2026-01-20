package prac.backendassingment.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.domain.model.AppliedDiscount;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscountResult {
    private Long finalPrice;
    private List<AppliedDiscount> discounts;
}
