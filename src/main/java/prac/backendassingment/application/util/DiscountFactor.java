package prac.backendassingment.application.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.domain.model.AppliedDiscount;
import prac.backendassingment.domain.model.Member;
import prac.backendassingment.domain.model.Order;
import prac.backendassingment.global.enums.PaymentMethod;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DiscountFactor {
    private Order order;
    private Member member;
    private PaymentMethod paymentMethod; // 필요시 추가
    // 추후 시간, 쿠폰 정보 등이 필요하면 여기 필드만 추가하면 됨
}
