package prac.backendassingment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.backendassingment.application.dto.PaymentCommitRequest;
import prac.backendassingment.application.util.DiscountFactor;
import prac.backendassingment.domain.model.*;
import prac.backendassingment.domain.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MemberService memberService;
    private final OrderService orderService;
    private final DiscountService discountService;

    public Payment findById(Long id){
        return paymentRepository.findById(id).orElseThrow(()->new IllegalArgumentException("유저가 존재하지 않음."));
    }

    @Transactional
    public Payment commitPayment(PaymentCommitRequest request){
        Order order = orderService.findById(request.getOrderId());
        Member member = memberService.findMemberById(order.getMemberId());

        DiscountFactor factor = new DiscountFactor(order, member, request.getPaymentMethod());
        Long finalPrice = discountService.calculateFinalPrice(factor);

        Payment payment = new Payment(
                request.getOrderId(),
                request.getPaymentMethod(),
                finalPrice
        );

        return paymentRepository.pay(payment);
    }
}
