package prac.backendassingment.application.util;

import prac.backendassingment.domain.model.DiscountCondition;

import java.util.List;

public interface DiscountConditionProvider {
    boolean support(DiscountFactor factor);
    List<DiscountCondition> getConditions(DiscountFactor factor);
}
