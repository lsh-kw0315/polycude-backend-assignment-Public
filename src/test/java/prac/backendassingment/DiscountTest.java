package prac.backendassingment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prac.backendassingment.application.service.*;
import prac.backendassingment.application.util.DiscountFactor;
import prac.backendassingment.application.util.MemberRankConditionProvider;
import prac.backendassingment.application.util.PaymentMethodConditionProvider;
import prac.backendassingment.domain.model.*;
import prac.backendassingment.domain.repository.DiscountRepository;
import prac.backendassingment.domain.service.DiscountCalculatorService;
import prac.backendassingment.global.enums.DiscountMethod;
import prac.backendassingment.global.enums.DiscountReason;
import prac.backendassingment.global.enums.MemberRank;
import prac.backendassingment.global.enums.PaymentMethod;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
public class DiscountTest {

    private DiscountService discountService;
    private DiscountCalculatorService calculatorService;
    @Mock
    private DiscountRepository discountRepository;

    @BeforeEach
    public void init(){
        calculatorService = new DiscountCalculatorService();

        discountService = new DiscountService(
                discountRepository,
                List.of(new MemberRankConditionProvider(), new PaymentMethodConditionProvider()),
                calculatorService
        );

    }

    @Test
    public void checkMemberRankDiscount(){
        //given
        Member member1 = new Member(1L, MemberRank.NORMAL);
        Member member2 = new Member(2L, MemberRank.VIP);
        Member member3 = new Member(3L, MemberRank.VVIP);

        Product product = new Product(1L, "apple", 1000L, 15L);

        DiscountPolicy vipPolicy = new DiscountPolicy(DiscountMethod.FIXED, new BigDecimal(1000));
        List<DiscountCondition> vipConditions = new ArrayList<>();
        DiscountCondition vipCondition = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name());
        vipConditions.add(vipCondition);

        DiscountPolicy vvipPolicy = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.9"));
        List<DiscountCondition> vvipConditions = new ArrayList<>();
        DiscountCondition vvipCondition = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VVIP.name());
        vvipConditions.add(vvipCondition);

        Discount vipDiscount = new Discount(1L, "VIP RANK DISCOUNT", vipPolicy, vipConditions);
        Discount vvipDiscount = new Discount(2L, "VVIP RANK DISCOUNT", vvipPolicy, vvipConditions);

        Order order1 = new Order(1L, 1L, List.of(new OrderItem(product.getId(), product.getName(), 5L, product.getPrice())));
        Order order2 = new Order(2L, 2L,List.of(new OrderItem(product.getId(), product.getName(), 5L, product.getPrice())));
        Order order3 = new Order(3L, 3L,List.of(new OrderItem(product.getId(), product.getName(), 5L, product.getPrice())));

        // [변경 후] - "VIP 조건이 리스트 안에 들어있으면" vipDiscount 반환
        BDDMockito.given(discountRepository.findAllByConditions(argThat(list ->
                list!= null && list.stream().anyMatch(c ->
                        c.getDiscountReason() == DiscountReason.MEMBER_RANK &&
                                c.getCondition().equals(MemberRank.VIP.name()))
        ))).willReturn(List.of(vipDiscount));

        // "VVIP 조건이 리스트 안에 들어있으면" vvipDiscount 반환
        BDDMockito.given(discountRepository.findAllByConditions(argThat(list ->
                list!= null &&list.stream().anyMatch(c ->
                        c.getDiscountReason() == DiscountReason.MEMBER_RANK &&
                                c.getCondition().equals(MemberRank.VVIP.name()))
        ))).willReturn(List.of(vvipDiscount));

        // "MemberRank 조건이 없는 경우" (즉, 일반 유저) - 빈 리스트 반환
        // 주의: 위 두 조건에 해당하지 않으면서 호출되는 경우를 대비해 설정
        BDDMockito.given(discountRepository.findAllByConditions(argThat(list ->
                list==null || list.stream().anyMatch(c ->
                        c.getDiscountReason() == DiscountReason.MEMBER_RANK &&
                                c.getCondition().equals(MemberRank.NORMAL.name()))
        ))).willReturn(List.of());

        //when
        Long finalPrice1 = discountService.calculateFinalPrice(new DiscountFactor(order1, member1, PaymentMethod.CREDIT));
        Long finalPrice2 = discountService.calculateFinalPrice(new DiscountFactor(order2, member2, PaymentMethod.CREDIT));
        Long finalPrice3 = discountService.calculateFinalPrice(new DiscountFactor(order3, member3, PaymentMethod.CREDIT));

        //then

        Assertions.assertThat(finalPrice1).isEqualTo(order1.getOriginalPrice());
        Assertions.assertThat(finalPrice2).isEqualTo(order2.getOriginalPrice() - 1000);
        Assertions.assertThat(finalPrice3).isEqualTo(new BigDecimal(order3.getOriginalPrice()).multiply(new BigDecimal("0.9")).longValue());

    }
}
