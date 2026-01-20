package prac.backendassingment.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.global.enums.PaymentMethod;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCommitRequest {
    private Long orderId;
    private PaymentMethod paymentMethod;
}
