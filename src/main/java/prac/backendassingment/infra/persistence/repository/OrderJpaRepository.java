package prac.backendassingment.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prac.backendassingment.infra.persistence.entity.OrderEntity;


public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
