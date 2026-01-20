package prac.backendassingment.domain.service;

import org.springframework.stereotype.Service;
import prac.backendassingment.domain.model.Discount;
import prac.backendassingment.domain.model.Order;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DiscountCalculatorService {

    //외부에서 이미 적절한 할인 정책을 찾아왔다고 가정
    public Long calculateTotalPrice(Order order, List<Discount> discounts){
        BigDecimal price = new BigDecimal(order.getOriginalPrice());

        for(Discount discount : discounts){
            // switch 문이 사라지고 다형성(혹은 도메인 메서드) 활용
            price = discount.getDiscountPolicy().applyDiscount(price);
        }

        // 가격이 0보다 작아지는 경우 처리
        return price.max(BigDecimal.ZERO).longValue();
    }
}
