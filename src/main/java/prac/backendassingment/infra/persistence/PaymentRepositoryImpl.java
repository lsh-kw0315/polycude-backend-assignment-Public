package prac.backendassingment.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import prac.backendassingment.domain.model.Payment;
import prac.backendassingment.domain.repository.PaymentRepository;
import prac.backendassingment.infra.persistence.entity.OrderEntity;
import prac.backendassingment.infra.persistence.entity.PaymentEntity;
import prac.backendassingment.infra.persistence.repository.OrderJpaRepository;
import prac.backendassingment.infra.persistence.repository.PaymentJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Optional<Payment> findById(Long id) {
        Optional<PaymentEntity> optional = paymentJpaRepository.findById(id);
        if(optional.isEmpty()) return Optional.empty();

        PaymentEntity paymentEntity = optional.get();

        return Optional.of(toDomain(paymentEntity));
    }

    @Override
    public Payment pay(Payment payment) {
        PaymentEntity paymentEntity = toEntity(payment);
        PaymentEntity saved = paymentJpaRepository.save(paymentEntity);

        return toDomain(saved);
    }

    private Payment toDomain(PaymentEntity paymentEntity){
        return new Payment(
                paymentEntity.getId(),
                paymentEntity.getOrder().getId(),
                paymentEntity.getPaymentMethod(),
                paymentEntity.getFinalPrice(),
                paymentEntity.getCreatedDate()
        );
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
