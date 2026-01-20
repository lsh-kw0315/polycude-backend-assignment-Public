package prac.backendassingment.domain.service;

import org.springframework.stereotype.Service;
import prac.backendassingment.application.dto.DiscountResult;
import prac.backendassingment.domain.model.AppliedDiscount;
import prac.backendassingment.domain.model.Discount;
import prac.backendassingment.domain.model.DiscountPolicy;
import prac.backendassingment.domain.model.Order;
import prac.backendassingment.global.enums.DiscountMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiscountCalculatorService {

    //외부에서 이미 적절한 할인 정책을 찾아왔다고 가정
    public DiscountResult calculateTotalPrice(Order order, List<Discount> discounts){
        BigDecimal price = new BigDecimal(order.getOriginalPrice());
        List<AppliedDiscount> appliedDiscounts = new ArrayList<>();

        Map<DiscountMethod, List<Discount>> discountMethodListMap =
                discounts.stream().collect(Collectors.groupingBy(
                        discount -> discount.getDiscountPolicy().getDiscountMethod()
                ));

        price = calculateDiscount(DiscountMethod.FIXED, discountMethodListMap, price, appliedDiscounts);
        price = calculateDiscount(DiscountMethod.PERCENTAGE, discountMethodListMap, price, appliedDiscounts);


        // 가격이 0보다 작아지는 경우 처리
        return new DiscountResult(price.max(BigDecimal.ZERO).longValue(), appliedDiscounts);
    }

    private BigDecimal calculateDiscount(DiscountMethod method, Map<DiscountMethod, List<Discount>> discountMethodListMap, BigDecimal price, List<AppliedDiscount> appliedDiscounts) {
        if(discountMethodListMap.get(method) == null){
            return price;
        }
        for(Discount discount : discountMethodListMap.get(method)){
            BigDecimal before = price;
            price = discount.getDiscountPolicy().applyDiscount(price);
            long discountedPrice = before.subtract(price).longValue();
            if(discountedPrice > 0){
                appliedDiscounts.add(new AppliedDiscount(discount, discountedPrice));
            }
        }
        return price;
    }
}
