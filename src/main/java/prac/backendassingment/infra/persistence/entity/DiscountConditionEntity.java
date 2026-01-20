package prac.backendassingment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.global.enums.DiscountReason;

@Entity
@Table(name = "discount_condition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class DiscountConditionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_policy_id")
    private DiscountPolicyEntity discountPolicy;

    @Embedded
    private DiscountConditionValue conditionValue;


    public DiscountConditionEntity(DiscountPolicyEntity discountPolicy,DiscountConditionValue conditionValue){
        this(null,  discountPolicy, conditionValue);
    }
}
