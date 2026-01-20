package prac.backendassingment.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import prac.backendassingment.domain.model.*;
import prac.backendassingment.domain.repository.PaymentRepository;
import prac.backendassingment.infra.persistence.entity.AppliedDiscountEntity;
import prac.backendassingment.infra.persistence.entity.OrderEntity;
import prac.backendassingment.infra.persistence.entity.PaymentEntity;
import prac.backendassingment.infra.persistence.repository.AppliedDiscountJpaRepository;
import prac.backendassingment.infra.persistence.repository.OrderJpaRepository;
import prac.backendassingment.infra.persistence.repository.PaymentJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final AppliedDiscountJpaRepository appliedDiscountJpaRepository;

    @Override
    public Optional<Payment> findById(Long id) {
        Optional<PaymentEntity> optional = paymentJpaRepository.findById(id);
        if(optional.isEmpty()) return Optional.empty();

        PaymentEntity paymentEntity = optional.get();
        List<AppliedDiscountEntity> appliedDiscountEntities = appliedDiscountJpaRepository.findByPayment(paymentEntity);
        return Optional.of(toDomain(paymentEntity, appliedDiscountEntities));
    }

    @Override
    public Payment pay(Payment payment) {
        PaymentEntity paymentEntity = toEntity(payment);
        PaymentEntity saved = paymentJpaRepository.save(paymentEntity);
        List<AppliedDiscount> discounts = payment.getAppliedDiscounts();
        List<AppliedDiscountEntity> savedHistory = appliedDiscountJpaRepository.saveAll(
          toAppliedDiscountEntities(saved.getId(), discounts)
        );

        return toDomain(saved, savedHistory);
    }

    private Payment toDomain(PaymentEntity paymentEntity, List<AppliedDiscountEntity> appliedDiscountEntities){
        return new Payment(
                paymentEntity.getId(),
                paymentEntity.getOrder().getId(),
                paymentEntity.getPaymentMethod(),
                paymentEntity.getFinalPrice(),
                paymentEntity.getCreatedDate(),
                toAppliedDiscountDomains(appliedDiscountEntities)
        );
    }

    private List<AppliedDiscount> toAppliedDiscountDomains(List<AppliedDiscountEntity> appliedDiscountEntities){
        List<AppliedDiscount> result = new ArrayList<>();
        //"과거의 할인"이 되어버렸고, 할인 조건들이 하나의 정책을 가리킨다면 적어도 그 이름은 같을 것이다.
        Map<String, List<AppliedDiscountEntity>> nameDiscountMap =
                appliedDiscountEntities.stream().collect(Collectors.groupingBy(AppliedDiscountEntity::getName));

        for(String key: nameDiscountMap.keySet()){
            List<AppliedDiscountEntity> list = nameDiscountMap.get(key);

            AppliedDiscountEntity entity = list.get(0);
            result.add(
                        new AppliedDiscount(
                                new Discount(key, new DiscountPolicy(entity.getDiscountPolicyValue().getDiscountMethod(), entity.getDiscountPolicyValue().getDiscountAmount()),
                                        list.stream()
                                                .map(e -> new DiscountCondition(e.getDiscountConditionValue().getDiscountReason(), e.getDiscountConditionValue().getCondition()))
                                                .collect(Collectors.toList())),
                                entity.getDiscountedPrice()
                        )
            );
        }

        return result;
    }

    private List<AppliedDiscountEntity> toAppliedDiscountEntities(Long paymentId, List<AppliedDiscount> appliedDiscounts){
        List<AppliedDiscountEntity> result= new ArrayList<>();
        PaymentEntity proxy = paymentJpaRepository.getReferenceById(paymentId);
        for(AppliedDiscount appliedDiscount : appliedDiscounts){
            for(DiscountCondition condition : appliedDiscount.getConditions()){
                result.add(
                        new AppliedDiscountEntity(
                                proxy,
                                appliedDiscount.getName(),
                                appliedDiscount.getDiscountedPrice(),
                                appliedDiscount.getDiscountPolicy().getDiscountMethod(),
                                appliedDiscount.getDiscountPolicy().getDiscountAmount(),
                                condition.getDiscountReason(),
                                condition.getCondition()

                        )
                );
            }
        }

        return result;
    }

    private PaymentEntity toEntity(Payment payment){
        OrderEntity proxy = orderJpaRepository.getReferenceById(payment.getOrderId());
        return new PaymentEntity(
                payment.getId(),
                proxy,
                payment.getPaymentMethod(),
                payment.getFinalPrice(),
                payment.getCreatedAt()
        );
    }
}
