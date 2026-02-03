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
import prac.backendassingment.global.enums.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class DiscountTest {

    private DiscountService discountService;
    private DiscountCalculatorService calculatorService;
    @Mock
    private DiscountRepository discountRepository;

    @BeforeEach
    public void init() {
        calculatorService = new DiscountCalculatorService();

        discountService = new DiscountService(
                discountRepository,
                List.of(new MemberRankConditionProvider(), new PaymentMethodConditionProvider()),
                calculatorService
        );

    }

    @Test
    public void checkMemberRankDiscount() {
        //given
        Member member1 = new Member(1L, MemberRank.NORMAL, "a1", "1234", "user1" , MemberRole.USER, null);
        Member member2 = new Member(2L, MemberRank.VIP,"a2", "1234", "user2" , MemberRole.USER, null);
        Member member3 = new Member(3L, MemberRank.VVIP,"a3", "1234", "user3" , MemberRole.USER, null);

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
        Order order2 = new Order(2L, 2L, List.of(new OrderItem(product.getId(), product.getName(), 5L, product.getPrice())));
        Order order3 = new Order(3L, 3L, List.of(new OrderItem(product.getId(), product.getName(), 5L, product.getPrice())));

        // [변경 후] - "VIP 조건이 리스트 안에 들어있으면" vipDiscount 반환
        BDDMockito.given(discountRepository.findAllByConditions(argThat(list ->
                list != null && list.stream().anyMatch(c ->
                        c.getDiscountReason() == DiscountReason.MEMBER_RANK &&
                                c.getCondition().equals(MemberRank.VIP.name()))
        ))).willReturn(List.of(vipDiscount));

        // "VVIP 조건이 리스트 안에 들어있으면" vvipDiscount 반환
        BDDMockito.given(discountRepository.findAllByConditions(argThat(list ->
                list != null && list.stream().anyMatch(c ->
                        c.getDiscountReason() == DiscountReason.MEMBER_RANK &&
                                c.getCondition().equals(MemberRank.VVIP.name()))
        ))).willReturn(List.of(vvipDiscount));

        // "MemberRank 조건이 없는 경우" (즉, 일반 유저) - 빈 리스트 반환
        // 주의: 위 두 조건에 해당하지 않으면서 호출되는 경우를 대비해 설정
        BDDMockito.given(discountRepository.findAllByConditions(argThat(list ->
                list == null || list.stream().anyMatch(c ->
                        c.getDiscountReason() == DiscountReason.MEMBER_RANK &&
                                c.getCondition().equals(MemberRank.NORMAL.name()))
        ))).willReturn(List.of());

        //when
        Long finalPrice1 = discountService.calculateFinalPrice(new DiscountFactor(order1, member1, PaymentMethod.CREDIT)).getFinalPrice();
        Long finalPrice2 = discountService.calculateFinalPrice(new DiscountFactor(order2, member2, PaymentMethod.CREDIT)).getFinalPrice();
        Long finalPrice3 = discountService.calculateFinalPrice(new DiscountFactor(order3, member3, PaymentMethod.CREDIT)).getFinalPrice();

        //then

        Assertions.assertThat(finalPrice1).isEqualTo(order1.getOriginalPrice());
        Assertions.assertThat(finalPrice2).isEqualTo(order2.getOriginalPrice() - 1000);
        Assertions.assertThat(finalPrice3).isEqualTo(new BigDecimal(order3.getOriginalPrice()).multiply(new BigDecimal("0.9")).longValue());

    }

    @Test
    public void 일반회원과_포인트() {
        Member member1 = new Member(1L, MemberRank.NORMAL,"a1", "1234", "user1" , MemberRole.USER, null);
        Product product = new Product(1L, "apple", 1000L, 15L);

        DiscountPolicy vipPolicy = new DiscountPolicy(DiscountMethod.FIXED, new BigDecimal(1000));
        List<DiscountCondition> vipConditions = new ArrayList<>();
        DiscountCondition vipCondition = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name());
        vipConditions.add(vipCondition);

        DiscountPolicy vvipPolicy = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.9"));
        List<DiscountCondition> vvipConditions = new ArrayList<>();
        DiscountCondition vvipCondition = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VVIP.name());
        vvipConditions.add(vvipCondition);

        DiscountPolicy pointPolicy = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.95"));
        List<DiscountCondition> pointConditions = new ArrayList<>();
        DiscountCondition pointCondition = new DiscountCondition(DiscountReason.PAYMENT_METHOD, PaymentMethod.POINT.name());
        pointConditions.add(pointCondition);

        Discount vipDiscount = new Discount(1L, "VIP RANK DISCOUNT", vipPolicy, vipConditions);
        Discount vvipDiscount = new Discount(2L, "VVIP RANK DISCOUNT", vvipPolicy, vvipConditions);
        Discount pointDiscount = new Discount(3L, "POINT DISCOUNT", pointPolicy, pointConditions);

        Order order1 = new Order(1L, 1L, List.of(new OrderItem(product.getId(), product.getName(), 5L, product.getPrice())));

        List<DiscountCondition> arg = List.of(new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.NORMAL.name()),pointCondition);

        // 예시: 리스트 안에 VIP 조건도 있고, 동시에 결제수단 조건도 있어야 한다.
        BDDMockito.given(
                discountRepository.findAllByConditions(eq(arg))).willReturn(List.of(pointDiscount));

        //when
        Long finalPrice1 = discountService.calculateFinalPrice(new DiscountFactor(order1, member1, PaymentMethod.POINT)).getFinalPrice();
        //then
        Assertions.assertThat(finalPrice1).isEqualTo(new BigDecimal(order1.getOriginalPrice()).multiply(new BigDecimal("0.95")).longValue());
    }

    @Test
    public void VIP와_포인트() {

        Member member1 = new Member(1L, MemberRank.VIP,"a1", "1234", "user1" , MemberRole.USER, null);
        Product product = new Product(1L, "apple", 1000L, 15L);

        DiscountPolicy vipPolicy = new DiscountPolicy(DiscountMethod.FIXED, new BigDecimal(1000));
        List<DiscountCondition> vipConditions = new ArrayList<>();
        DiscountCondition vipCondition = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name());
        vipConditions.add(vipCondition);

        DiscountPolicy vvipPolicy = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.9"));
        List<DiscountCondition> vvipConditions = new ArrayList<>();
        DiscountCondition vvipCondition = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VVIP.name());
        vvipConditions.add(vvipCondition);

        DiscountPolicy pointPolicy = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.95"));
        List<DiscountCondition> pointConditions = new ArrayList<>();
        DiscountCondition pointCondition = new DiscountCondition(DiscountReason.PAYMENT_METHOD, PaymentMethod.POINT.name());
        pointConditions.add(pointCondition);

        Discount vipDiscount = new Discount(1L, "VIP RANK DISCOUNT", vipPolicy, vipConditions);
        Discount vvipDiscount = new Discount(2L, "VVIP RANK DISCOUNT", vvipPolicy, vvipConditions);
        Discount pointDiscount = new Discount(3L, "POINT DISCOUNT", pointPolicy, pointConditions);

        Order order1 = new Order(1L, 1L, List.of(new OrderItem(product.getId(), product.getName(), 5L, product.getPrice())));

        List<DiscountCondition> arg = List.of(pointCondition, vipCondition);

        // 예시: 리스트 안에 VIP 조건도 있고, 동시에 결제수단 조건도 있어야 한다.
        BDDMockito.given(
                discountRepository.findAllByConditions(argThat(list -> {
                    if (list == null) return false;

                    // 1. VIP 조건이 리스트 어딘가에 있는지 확인
                    boolean hasVip = list.stream().anyMatch(c ->
                            c.getDiscountReason() == DiscountReason.MEMBER_RANK &&
                                    MemberRank.VIP.name().equals(c.getCondition())
                    );

                    // 2. PAYMENT_METHOD(예: CARD) 조건이 리스트 어딘가에 있는지 확인
                    boolean hasPaymentMethod = list.stream().anyMatch(c ->
                            c.getDiscountReason() == DiscountReason.PAYMENT_METHOD &&
                                    c.getCondition().equals(PaymentMethod.POINT.name()) // 예시값
                    );

                    // 3. "둘 다" true여야만 최종 매칭 성공
                    return hasVip && hasPaymentMethod;

                }))).willReturn(List.of(pointDiscount, vipDiscount));

        //when
        Long finalPrice1 = discountService.calculateFinalPrice(new DiscountFactor(order1, member1,PaymentMethod.POINT)).getFinalPrice();
        BigDecimal intermediate = new BigDecimal(order1.getOriginalPrice()).subtract(new BigDecimal(1000));
        BigDecimal expected = intermediate.multiply(new BigDecimal("0.95"));
        //then
        Assertions.assertThat(finalPrice1).isEqualTo(expected.longValue());


    }

    @Test
    public void VVIP와_포인트() {
        Member member1 = new Member(1L, MemberRank.VVIP,"a1", "1234", "user1" , MemberRole.USER, null);
        Product product = new Product(1L, "apple", 1000L, 15L);

        DiscountPolicy vipPolicy = new DiscountPolicy(DiscountMethod.FIXED, new BigDecimal(1000));
        List<DiscountCondition> vipConditions = new ArrayList<>();
        DiscountCondition vipCondition = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name());
        vipConditions.add(vipCondition);

        DiscountPolicy vvipPolicy = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.9"));
        List<DiscountCondition> vvipConditions = new ArrayList<>();
        DiscountCondition vvipCondition = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VVIP.name());
        vvipConditions.add(vvipCondition);

        DiscountPolicy pointPolicy = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.95"));
        List<DiscountCondition> pointConditions = new ArrayList<>();
        DiscountCondition pointCondition = new DiscountCondition(DiscountReason.PAYMENT_METHOD, PaymentMethod.POINT.name());
        pointConditions.add(pointCondition);

        Discount vipDiscount = new Discount(1L, "VIP RANK DISCOUNT", vipPolicy, vipConditions);
        Discount vvipDiscount = new Discount(2L, "VVIP RANK DISCOUNT", vvipPolicy, vvipConditions);
        Discount pointDiscount = new Discount(3L, "POINT DISCOUNT", pointPolicy, pointConditions);

        Order order1 = new Order(1L, 1L, List.of(new OrderItem(product.getId(), product.getName(), 5L, product.getPrice())));

        List<DiscountCondition> arg = List.of(pointCondition, vvipCondition);

        // 예시: 리스트 안에 VIP 조건도 있고, 동시에 결제수단 조건도 있어야 한다.
        BDDMockito.given(
                discountRepository.findAllByConditions(argThat(list -> {
                    if (list == null) return false;

                    // 1. VIP 조건이 리스트 어딘가에 있는지 확인
                    boolean hasVVip = list.stream().anyMatch(c ->
                            c.getDiscountReason() == DiscountReason.MEMBER_RANK &&
                                    MemberRank.VVIP.name().equals(c.getCondition())
                    );

                    // 2. PAYMENT_METHOD(예: CARD) 조건이 리스트 어딘가에 있는지 확인
                    boolean hasPaymentMethod = list.stream().anyMatch(c ->
                            c.getDiscountReason() == DiscountReason.PAYMENT_METHOD &&
                                    c.getCondition().equals(PaymentMethod.POINT.name()) // 예시값
                    );

                    // 3. "둘 다" true여야만 최종 매칭 성공
                    return hasVVip && hasPaymentMethod;

                }))).willReturn(List.of(pointDiscount, vvipDiscount));

        //when
        Long finalPrice1 = discountService.calculateFinalPrice(new DiscountFactor(order1, member1, PaymentMethod.POINT)).getFinalPrice();

        //then
        Assertions.assertThat(finalPrice1).isEqualTo(new BigDecimal(order1.getOriginalPrice()).multiply(new BigDecimal("0.9")).multiply(new BigDecimal("0.95")).longValue());

    }
}
