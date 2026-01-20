package prac.backendassingment.application.util;

import org.springframework.stereotype.Component;
import prac.backendassingment.domain.model.DiscountCondition;
import prac.backendassingment.global.enums.DiscountReason;

import java.util.List;

@Component
public class PaymentMethodConditionProvider implements DiscountConditionProvider{
    @Override
    public boolean support(DiscountFactor factor) {
        return factor.getPaymentMethod() != null;
    }

    @Override
    public List<DiscountCondition> getConditions(DiscountFactor factor) {
        return List.of(
                new DiscountCondition(DiscountReason.PAYMENT_METHOD, factor.getPaymentMethod().name())
        );
    }
}
