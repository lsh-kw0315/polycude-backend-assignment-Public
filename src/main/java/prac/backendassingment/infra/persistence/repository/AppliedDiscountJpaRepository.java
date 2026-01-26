package prac.backendassingment.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prac.backendassingment.infra.persistence.entity.AppliedDiscountEntity;
import prac.backendassingment.infra.persistence.entity.PaymentEntity;

import java.util.List;

public interface AppliedDiscountJpaRepository extends JpaRepository<AppliedDiscountEntity, Long> {
    List<AppliedDiscountEntity> findByPayment(PaymentEntity payment);
}
