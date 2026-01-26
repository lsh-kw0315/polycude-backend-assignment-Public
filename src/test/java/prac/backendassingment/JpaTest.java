package prac.backendassingment;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import prac.backendassingment.application.dto.*;
import prac.backendassingment.application.service.*;
import prac.backendassingment.domain.model.*;
import prac.backendassingment.global.enums.DiscountMethod;
import prac.backendassingment.global.enums.DiscountReason;
import prac.backendassingment.global.enums.MemberRank;
import prac.backendassingment.global.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Profile("default")
public class JpaTest {
    @Autowired
    private DiscountService discountService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ProductService productService;

    @Test
    @Rollback
    public void testMember(){
        Member member1 = memberService.joinMember(new MemberJoinRequest(MemberRank.NORMAL));
        Member find = memberService.findMemberById(member1.getId());

        Assertions.assertThat(find).isEqualTo(member1);

        //=====================

        Member modified = memberService.modifyMember(new MemberUpdateRequest(member1.getId(), MemberRank.VVIP));

        Assertions.assertThat(modified.getRank()).isEqualTo(MemberRank.VVIP);
    }

    @Test
    @Rollback
    public void testDiscount(){
        DiscountCondition condition1 = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name());
        DiscountCondition condition2 = new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VVIP.name());
        DiscountCondition condition3 = new DiscountCondition(DiscountReason.PAYMENT_METHOD, PaymentMethod.POINT.name());
        DiscountCondition condition4 = new DiscountCondition(DiscountReason.PAYMENT_METHOD, PaymentMethod.CREDIT.name());

        List<DiscountCondition> conditions1 =  new ArrayList<>();
        conditions1.add(condition1);
        conditions1.add(condition3);
        DiscountPolicy policy1 = new DiscountPolicy(DiscountMethod.FIXED, new BigDecimal(1000));
        Discount discount1 = new Discount("1000원할인",policy1 ,conditions1);

        Discount saved1 = discountService.saveDiscount(new DiscountSaveRequest(
                discount1.getName(),
                discount1.getDiscountPolicy(),
                discount1.getConditions()
        ));
        Assertions.assertThat(saved1.getId()).isNotNull();
        Assertions.assertThat(saved1.getDiscountPolicy()).isEqualTo(discount1.getDiscountPolicy());
        Assertions.assertThat(saved1.getConditions()).containsExactlyInAnyOrderElementsOf(discount1.getConditions());
        Assertions.assertThat(saved1.getName()).isEqualTo(discount1.getName());

        //=================

        List<DiscountCondition> conditions2 =  new ArrayList<>();
        conditions2.add(condition2);
        conditions2.add(condition3);
        DiscountPolicy policy2 = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.9"));
        Discount discount2 = new Discount("10퍼 할인",policy2 ,conditions2);
        Discount saved2 = discountService.saveDiscount(new DiscountSaveRequest(
                discount2.getName(),
                discount2.getDiscountPolicy(),
                discount2.getConditions()
        ));
        Assertions.assertThat(saved2.getId()).isNotNull();
        Assertions.assertThat(saved2.getDiscountPolicy()).isEqualTo(discount2.getDiscountPolicy());
        Assertions.assertThat(saved2.getConditions()).containsExactlyInAnyOrderElementsOf(discount2.getConditions());
        Assertions.assertThat(saved2.getName()).isEqualTo(discount2.getName());

        List<DiscountCondition> search = new ArrayList<>();
        search.add(new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name()));
        search.add(new DiscountCondition(DiscountReason.PAYMENT_METHOD, PaymentMethod.POINT.name()));

        discountService.searchDiscount(new DiscountSearchRequest(
                search
        )).forEach(
               discount -> Assertions.assertThat(discount.getConditions()).containsAnyElementsOf(search)
        );

        DiscountPolicy discountPolicy = new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.5"));
        List<DiscountCondition> conditions3 = new ArrayList<>();
        conditions3.add(condition4);
        conditions3.add(condition2);
        saved2 = discountService.updateDiscount(new DiscountUpdateRequest(
                saved2.getId(),
                saved2.getName(),
                discountPolicy,
                conditions3
        ));
        System.out.println("saved2 Id: "+saved2.getId());
        Assertions.assertThat(saved2.getConditions()).containsExactlyInAnyOrderElementsOf(conditions3);
        Assertions.assertThat(saved2.getDiscountPolicy()).isEqualTo(discountPolicy);

        discountService.deleteDiscountById(saved1.getId());

        Assertions.assertThatThrownBy(() -> discountService.findDiscountById(saved1.getId())).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @Rollback
    public void testProduct(){
        ProductAddRequest productAddRequest1 = new ProductAddRequest("상품1", 1000L, 15L);
        Product product1 = productService.addNewProduct(productAddRequest1);

        ProductAddRequest productAddRequest2 = new ProductAddRequest("상품2", 1000L, 15L);
        Product product2 = productService.addNewProduct(productAddRequest2);

        Product saved1 = productService.findProductById(product1.getId());
        Product saved2 = productService.findProductById(product2.getId());

        Assertions.assertThat(saved1).isEqualTo(product1);
        Assertions.assertThat(saved2).isEqualTo(product2);

        //======================

        productService.removeProduct(saved1.getId());
        Assertions.assertThatThrownBy(()->productService.findProductById(saved1.getId())).isInstanceOf(IllegalArgumentException.class);

        //=====================

        Assertions.assertThatThrownBy(
                ()->productService.decreaseStock(new ProductStockRequest(saved2.getId(), 20L))
        ).isInstanceOf(IllegalArgumentException.class);

        //================

        Assertions.assertThatThrownBy(
                ()->productService.increaseStock(new ProductStockRequest(saved2.getId(), 0L))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Rollback
    public void testOrder(){
        ProductAddRequest productAddRequest1 = new ProductAddRequest("상품1", 1000L, 15L);
        Product product1 = productService.addNewProduct(productAddRequest1);

        ProductAddRequest productAddRequest2 = new ProductAddRequest("상품2", 1500L, 10L);
        Product product2 = productService.addNewProduct(productAddRequest2);

        Product saved1 = productService.findProductById(product1.getId());
        Product saved2 = productService.findProductById(product2.getId());

        //================
        Member member1 = memberService.joinMember(new MemberJoinRequest(MemberRank.NORMAL));
        List<OrderItem> orderItems = List.of(
                new OrderItem(saved1.getId(), saved1.getName(),5L, saved1.getPrice()),
                new OrderItem(saved2.getId(), saved2.getName(),5L, saved2.getPrice())
                );

        Order order = orderService.submitOrder(new OrderSubmitRequest(member1.getId(), orderItems));

        Order saved = orderService.findById(order.getId());
        Assertions.assertThat(order.getId()).isEqualTo(saved.getId());

        OrderItem orderItem1 = order.getOrderItems().get(0);
        OrderItem orderItem2 = order.getOrderItems().get(1);

        Product afterOrder1 = productService.findProductById(saved1.getId());
        Product afterOrder2 = productService.findProductById(saved2.getId());
        Assertions.assertThat(orderItem1.calculateTotalPrice()).isEqualTo(5000L);
        Assertions.assertThat(orderItem2.calculateTotalPrice()).isEqualTo(7500L);
        Assertions.assertThat(afterOrder1.getTotalAmount()).isEqualTo(10L);
        Assertions.assertThat(afterOrder2.getTotalAmount()).isEqualTo(5L);

    }

    @Test
    @Rollback
    public void testPayment(){
        ProductAddRequest productAddRequest1 = new ProductAddRequest("상품1", 1000L, 15L);
        Product product1 = productService.addNewProduct(productAddRequest1);

        ProductAddRequest productAddRequest2 = new ProductAddRequest("상품2", 1500L, 10L);
        Product product2 = productService.addNewProduct(productAddRequest2);

        //===============

        Discount discount1 = discountService.saveDiscount(
                new DiscountSaveRequest(
                        "1000원 할인",
                        new DiscountPolicy(DiscountMethod.FIXED, new BigDecimal(1000)),
                        List.of(new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name()))
                )
        );

        Discount discount2 = discountService.saveDiscount(
                new DiscountSaveRequest(
                        "10% 할인",
                        new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.9")),
                        List.of(new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VVIP.name()))
                )
        );

        //================
        Member member1 = memberService.joinMember(new MemberJoinRequest(MemberRank.VIP));
        Member member2 = memberService.joinMember(new MemberJoinRequest(MemberRank.VVIP));

        List<OrderItem> orderItems1 = List.of(
                new OrderItem(product1.getId(), product1.getName(),5L, product1.getPrice()),
                new OrderItem(product2.getId(), product2.getName(),5L, product2.getPrice())
        );

        List<OrderItem> orderItems2 = List.of(
                new OrderItem(product1.getId(), product1.getName(),5L, product1.getPrice()),
                new OrderItem(product2.getId(), product2.getName(),5L, product2.getPrice())
        );

        Order order1 = orderService.submitOrder(new OrderSubmitRequest(member1.getId(), orderItems1));
        Order order2 = orderService.submitOrder(new OrderSubmitRequest(member2.getId(), orderItems2));

        Payment payment1 = paymentService.commitPayment(
                new PaymentCommitRequest(order1.getId(), PaymentMethod.CREDIT)
        );

        Payment payment2 = paymentService.commitPayment(
                new PaymentCommitRequest(order2.getId(), PaymentMethod.CREDIT)
        );

        Assertions.assertThat(payment1.getCreatedAt())
                .isNotNull();

        Assertions.assertThat(payment2.getCreatedAt())
                .isNotNull();

        System.out.println(payment1.getCreatedAt());
        System.out.println(payment2.getCreatedAt());

        Long expected1 = new BigDecimal(1000*5 + 1500*5).subtract(new BigDecimal(1000)).longValue();
        Long expected2 = new BigDecimal(1000*5 + 1500*5).multiply(new BigDecimal("0.9")).longValue();
        Assertions.assertThat(payment1.getFinalPrice())
                .isEqualTo(expected1);

        Assertions.assertThat(payment2.getFinalPrice())
                .isEqualTo(expected2);
    }

    @Test
    @Rollback
    public void 정책변경_삭제_테스트(){
        ProductAddRequest productAddRequest1 = new ProductAddRequest("상품1", 1000L, 15L);
        Product product1 = productService.addNewProduct(productAddRequest1);

        ProductAddRequest productAddRequest2 = new ProductAddRequest("상품2", 1500L, 10L);
        Product product2 = productService.addNewProduct(productAddRequest2);

        //===============

        Discount discount1 = discountService.saveDiscount(
                new DiscountSaveRequest(
                        "1000원 할인",
                        new DiscountPolicy(DiscountMethod.FIXED, new BigDecimal(1000)),
                        List.of(new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name()))
                )
        );

        Discount discount2 = discountService.saveDiscount(
                new DiscountSaveRequest(
                        "10% 할인",
                        new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.9")),
                        List.of(new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VVIP.name()))
                )
        );

        Discount discount3 = discountService.saveDiscount(
                new DiscountSaveRequest(
                        "5% 할인",
                        new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.95")),
                        List.of(new DiscountCondition(DiscountReason.PAYMENT_METHOD, PaymentMethod.POINT.name()),
                                new DiscountCondition(DiscountReason.PAYMENT_METHOD, PaymentMethod.CREDIT.name()))
                )
        );

        //================
        Member member1 = memberService.joinMember(new MemberJoinRequest(MemberRank.VIP));
        Member member2 = memberService.joinMember(new MemberJoinRequest(MemberRank.VVIP));

        List<OrderItem> orderItems1 = List.of(
                new OrderItem(product1.getId(), product1.getName(),5L, product1.getPrice()),
                new OrderItem(product2.getId(), product2.getName(),5L, product2.getPrice())
        );

        List<OrderItem> orderItems2 = List.of(
                new OrderItem(product1.getId(), product1.getName(),5L, product1.getPrice()),
                new OrderItem(product2.getId(), product2.getName(),5L, product2.getPrice())
        );

        Order order1 = orderService.submitOrder(new OrderSubmitRequest(member1.getId(), orderItems1));
        Order order2 = orderService.submitOrder(new OrderSubmitRequest(member2.getId(), orderItems2));

        Payment payment1 = paymentService.commitPayment(
                new PaymentCommitRequest(order1.getId(), PaymentMethod.POINT)
        );

        Payment payment2 = paymentService.commitPayment(
                new PaymentCommitRequest(order2.getId(), PaymentMethod.CREDIT)
        );

        //삭제
        discountService.deleteDiscountById(discount1.getId());
        Payment find1 = paymentService.findById(payment1.getId());

        Assertions.assertThatThrownBy(()->discountService.findDiscountById(discount1.getId())).isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThat(payment1.getAppliedDiscounts())    .usingRecursiveComparison()
                .ignoringFields("createdDate") // 1. 시간 필드 무시 (필드명 입력)
                .ignoringCollectionOrder()                     // 2. 순서 무시 (= containsExactlyInAnyOrder)
                .isEqualTo(find1.getAppliedDiscounts());
        System.out.println("payment1:"+payment1.getAppliedDiscounts());
        System.out.println("find1:"+find1.getAppliedDiscounts());

        //수정
        discountService.updateDiscount(
                new DiscountUpdateRequest(discount2.getId(),
                        "20% 할인",
                        new DiscountPolicy(DiscountMethod.PERCENTAGE, new BigDecimal("0.8")),
                        List.of(new DiscountCondition(DiscountReason.MEMBER_RANK, MemberRank.VIP.name()))
                        )
        );

        Discount modified = discountService.findDiscountById(discount2.getId());
        Assertions.assertThat(modified).isNotEqualTo(discount2);
        System.out.println("discount2:"+discount2);
        System.out.println("modified:"+modified);

        Payment find2 = paymentService.findById(payment2.getId());
        Assertions.assertThat(payment2.getAppliedDiscounts())    .usingRecursiveComparison()
                .ignoringFields("createdDate") // 1. 시간 필드 무시 (필드명 입력)
                .ignoringCollectionOrder()                     // 2. 순서 무시 (= containsExactlyInAnyOrder)
                .isEqualTo(find2.getAppliedDiscounts());

        System.out.println("payment2:"+payment1.getAppliedDiscounts());
        System.out.println("find2:"+find1.getAppliedDiscounts());
    }
}
