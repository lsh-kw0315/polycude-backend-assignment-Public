package prac.backendassingment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import prac.backendassingment.global.enums.DiscountReason;
import prac.backendassingment.global.enums.DiscountMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "applied_discount")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class AppliedDiscountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

    private String name;

    private Long discountedPrice;

    @Embedded
    private DiscountPolicyValue discountPolicyValue;

    @Embedded
    private DiscountConditionValue discountConditionValue;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;


    public AppliedDiscountEntity(PaymentEntity payment, String name, Long discountedPrice, DiscountMethod discountMethod, BigDecimal discountAmount, DiscountReason discountReason, String condition, LocalDateTime createdDate){
        this(null, payment, name, discountedPrice, new DiscountPolicyValue(discountMethod, discountAmount), new DiscountConditionValue(discountReason, condition), createdDate);
    }

    public AppliedDiscountEntity(PaymentEntity payment, String name, Long discountedPrice, DiscountMethod discountMethod, BigDecimal discountAmount, DiscountReason discountReason, String condition){
        this(null, payment, name, discountedPrice, new DiscountPolicyValue(discountMethod, discountAmount), new DiscountConditionValue(discountReason, condition), null);
    }


}
