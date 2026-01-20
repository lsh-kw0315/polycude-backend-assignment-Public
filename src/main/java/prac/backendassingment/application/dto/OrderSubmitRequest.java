package prac.backendassingment.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.domain.model.OrderItem;

import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderSubmitRequest {
    Long memberId;
    List<OrderItem> orderItems;
}
