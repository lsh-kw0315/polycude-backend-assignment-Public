package prac.backendassingment.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prac.backendassingment.infra.persistence.entity.OrderEntity;
import prac.backendassingment.infra.persistence.entity.OrderItemEntity;

import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findAllByOrder(OrderEntity order);
}
