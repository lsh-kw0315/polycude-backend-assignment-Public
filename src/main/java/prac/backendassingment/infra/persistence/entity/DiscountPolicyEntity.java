package prac.backendassingment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.global.enums.DiscountMethod;

import java.math.BigDecimal;


@Entity
@Table(name = "discount_policy")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class DiscountPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private DiscountPolicyValue policyValue;

    public DiscountPolicyEntity(Long id, String name, DiscountMethod discountMethod, BigDecimal discountAmount){
        this(id, name, new DiscountPolicyValue(discountMethod, discountAmount));
    }

    public void updatePolicy(String name, DiscountMethod discountMethod, BigDecimal discountAmount){
        this.name = name;
        policyValue = new DiscountPolicyValue(discountMethod, discountAmount);
    }

}
