package prac.backendassingment.domain.repository;

import prac.backendassingment.domain.model.Payment;

import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> findById(Long id);
    Payment pay(Payment payment);
}
