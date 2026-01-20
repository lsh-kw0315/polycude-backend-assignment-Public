package prac.backendassingment.domain.model;

import lombok.*;
import prac.backendassingment.global.enums.DiscountMethod;
import prac.backendassingment.global.enums.DiscountReason;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class Discount {
    private Long id; //DiscountPolicy의 id
    private String name;

    private DiscountPolicy discountPolicy;
    private List<DiscountCondition> conditions;

    public Discount(String name, DiscountPolicy discountPolicy, List<DiscountCondition> conditions){
        this(null, name, discountPolicy, conditions);
    }

    public Discount(Long id, String name, DiscountPolicy discountPolicy, List<DiscountCondition> conditions){
        this.id = id;

        if(name == null || name.isEmpty()) throw new IllegalArgumentException("할인의 이름을 적어주시기 바랍니다.");
        this.name = name;

        if(discountPolicy == null) throw new IllegalArgumentException("Discount Policy는 누락되어서는 안됩니다.");
        this.discountPolicy = discountPolicy;

        if(conditions == null || conditions.isEmpty()) throw new IllegalArgumentException("Discount Condition은 누락되어서는 안됩니다.");
        this.conditions = conditions;
    }

    public List<DiscountCondition> getConditions(){
        return Collections.unmodifiableList(conditions);
    }

    public void addConditions(DiscountCondition condition){
        if(condition == null) return;
        conditions.add(condition);
    }

    public void removeConditions(DiscountCondition condition){
        if(condition == null) return;
        if(conditions.contains(condition) && conditions.size() == 1) throw new IllegalArgumentException("할인 조건이 없는 할인을 만들 수 없습니다.");
        conditions.remove(condition);
    }


}
