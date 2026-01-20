package prac.backendassingment.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import prac.backendassingment.domain.model.Order;
import prac.backendassingment.domain.model.OrderItem;
import prac.backendassingment.domain.repository.OrderRepository;
import prac.backendassingment.infra.persistence.entity.MemberEntity;
import prac.backendassingment.infra.persistence.entity.OrderEntity;
import prac.backendassingment.infra.persistence.entity.OrderItemEntity;
import prac.backendassingment.infra.persistence.entity.ProductEntity;
import prac.backendassingment.infra.persistence.repository.MemberJpaRepository;
import prac.backendassingment.infra.persistence.repository.OrderItemJpaRepository;
import prac.backendassingment.infra.persistence.repository.OrderJpaRepository;
import prac.backendassingment.infra.persistence.repository.ProductJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Order> findById(Long id) {
        Optional<OrderEntity> orderOptional = orderJpaRepository.findById(id);
        if(orderOptional.isEmpty()) return Optional.empty();

        OrderEntity orderEntity = orderOptional.get();
        List<OrderItemEntity> orderItemEntities = orderItemJpaRepository.findAllByOrder(orderEntity);

        return Optional.of(toDomain(orderEntity, orderItemEntities));
    }

    @Override
    public Order submit(Order order) {
        OrderEntity orderEntity = orderJpaRepository.save(toOrderEntity(order));
        List<OrderItemEntity> orderItemEntities = orderItemJpaRepository.saveAll(toOrderItemEntityList(order, orderEntity));
        return toDomain(orderEntity, orderItemEntities);
    }

    private Order toDomain(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities){
        return new Order(
                orderEntity.getId(),
                orderEntity.getMember().getId(),
                orderItemEntities.stream()
                        .map(entity ->
                        new OrderItem(entity.getProduct().getId(), entity.getProductName(), entity.getQuantity(), entity.getPricePerProduct())
                        ).collect(Collectors.toList())
        );
    }

    private OrderEntity toOrderEntity(Order order){
        MemberEntity proxy = memberJpaRepository.getReferenceById(order.getMemberId());
        return new OrderEntity(
                order.getId(),
                proxy,
                order.getOriginalPrice()
        );
    }

    private List<OrderItemEntity> toOrderItemEntityList(Order order, OrderEntity orderEntity){
        return order.getOrderItems().stream().map(
                orderItem -> {
                    ProductEntity productProxy = productJpaRepository.getReferenceById(orderItem.getProductId());
                     return new OrderItemEntity(
                            orderItem.getId(),
                            productProxy,
                             orderEntity,
                            orderItem.getQuantity(),
                            orderItem.getPricePerProduct(),
                            orderItem.getProductName());
                }
        ).collect(Collectors.toList());
    }
}
