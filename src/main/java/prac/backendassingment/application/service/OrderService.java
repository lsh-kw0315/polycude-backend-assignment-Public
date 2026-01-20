package prac.backendassingment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.backendassingment.application.dto.OrderSubmitRequest;
import prac.backendassingment.application.dto.ProductStockRequest;
import prac.backendassingment.domain.model.Order;
import prac.backendassingment.domain.model.OrderItem;
import prac.backendassingment.domain.repository.OrderRepository;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public Order findById(Long id){
        return orderRepository.findById(id).orElseThrow(()->new IllegalArgumentException("유저가 존재하지 않음."));
    }

    @Transactional
    public Order submitOrder(OrderSubmitRequest request){
        Order order = new Order(request.getMemberId(), request.getOrderItems());
        Order submitted =  orderRepository.submit(order);

        //Product 재고 감소 시 누구는 1 -> 2, 누구는 2 -> 1 이렇게 접근하면 기본적인 쓰기 락에 의해 데드락이 걸릴 수 있다고 한다.
        submitted.getOrderItems()
                .stream()
                .sorted( Comparator.comparing(OrderItem::getProductId)).forEach(
                orderItem -> {
                    ProductStockRequest stockRequest = new ProductStockRequest(orderItem.getProductId(), orderItem.getQuantity());
                    productService.decreaseStock(stockRequest);
                }
        );

        return submitted;
    }


}
