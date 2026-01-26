package prac.backendassingment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.backendassingment.application.dto.DiscountResult;
import prac.backendassingment.application.dto.DiscountSaveRequest;
import prac.backendassingment.application.dto.DiscountSearchRequest;
import prac.backendassingment.application.dto.DiscountUpdateRequest;
import prac.backendassingment.application.util.DiscountConditionProvider;
import prac.backendassingment.application.util.DiscountFactor;
import prac.backendassingment.domain.model.Discount;
import prac.backendassingment.domain.model.DiscountCondition;
import prac.backendassingment.domain.repository.DiscountRepository;
import prac.backendassingment.domain.service.DiscountCalculatorService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final List<DiscountConditionProvider> providers; // 모든 Provider 주입
    private final DiscountCalculatorService calculatorService;

    @Transactional
    public Discount saveDiscount(DiscountSaveRequest request){
        Discount discount = new Discount(
                request.getName(),
                request.getDiscountPolicy(),
                request.getConditions()
        );

        return discountRepository.save(discount);
    }

    @Transactional
    public Discount updateDiscount(DiscountUpdateRequest request){
        Discount discount = new Discount(
                request.getId(),
                request.getName(),
                request.getDiscountPolicy(),
                request.getConditions()
        );

        return discountRepository.save(discount);
    }

    @Transactional
    public void deleteDiscountById(Long id){
        discountRepository.deleteById(id);
    }

    public Discount findDiscountById(Long id){
        Optional<Discount> discountOptional =  discountRepository.findById(id);
        return discountOptional.orElseThrow(()->new IllegalArgumentException("할인 정책이 존재하지 않음."));
    }

    public List<Discount> searchDiscount(DiscountSearchRequest request){
        return discountRepository.findAllByConditions(request.getConditions());
    }

    // PaymentService는 이 메서드만 호출하면 됨
    public DiscountResult calculateFinalPrice(DiscountFactor factor) {
        // 1. Context에서 조건들을 추출 (Provider들에게 위임)
        List<DiscountCondition> conditions = providers.stream()
                .filter(p -> p.support(factor))
                .map(p -> p.getConditions(factor))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // 2. 조건에 맞는 할인 정책 검색 (Repository)
        List<Discount> discounts = discountRepository.findAllByConditions(conditions);

        // 3. 계산 (CalculatorService 활용)
        return calculatorService.calculateTotalPrice(factor.getOrder(), discounts);
    }


}
