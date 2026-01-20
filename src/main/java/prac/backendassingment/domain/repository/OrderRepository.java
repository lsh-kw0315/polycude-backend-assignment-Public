package prac.backendassingment.domain.repository;

import prac.backendassingment.domain.model.Order;

import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(Long id);
    Order submit(Order order);
}
