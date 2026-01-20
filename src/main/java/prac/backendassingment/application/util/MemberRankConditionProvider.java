package prac.backendassingment.application.util;

import org.springframework.stereotype.Component;
import prac.backendassingment.domain.model.DiscountCondition;
import prac.backendassingment.global.enums.DiscountReason;

import java.util.List;

@Component
public class MemberRankConditionProvider implements DiscountConditionProvider{
    @Override
    public boolean support(DiscountFactor factor) {
        return factor.getMember() != null;
    }

    @Override
    public List<DiscountCondition> getConditions(DiscountFactor factor) {
        return List.of(
                new DiscountCondition(DiscountReason.MEMBER_RANK, factor.getMember().getRank().name())
        );
    }
}
