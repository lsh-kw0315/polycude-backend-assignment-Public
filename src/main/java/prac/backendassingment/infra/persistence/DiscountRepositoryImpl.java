package prac.backendassingment.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import prac.backendassingment.domain.model.Discount;
import prac.backendassingment.domain.model.DiscountCondition;
import prac.backendassingment.domain.model.DiscountPolicy;
import prac.backendassingment.domain.repository.DiscountRepository;
import prac.backendassingment.infra.persistence.entity.DiscountConditionEntity;
import prac.backendassingment.infra.persistence.entity.DiscountConditionValue;
import prac.backendassingment.infra.persistence.entity.DiscountPolicyEntity;
import prac.backendassingment.infra.persistence.repository.DiscountConditionJpaRepository;
import prac.backendassingment.infra.persistence.repository.DiscountPolicyJpaRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DiscountRepositoryImpl implements DiscountRepository {
    private final DiscountPolicyJpaRepository discountPolicyJpaRepository;
    private final DiscountConditionJpaRepository discountConditionJpaRepository;

    @Override
    public Discount save(Discount discount) {
        //save
        if(discount.getId() == null){
            DiscountPolicyEntity policy = toPolicyEntity(discount);
            DiscountPolicyEntity saved = discountPolicyJpaRepository.save(policy);

            //개선점: bulk insert 구현
            List<DiscountConditionEntity> conditions = discountConditionJpaRepository.saveAll(
                    toConditionEntitiyList(discount, saved.getId())
            );

            return toDomain(saved, conditions);

        }

        DiscountPolicyEntity target = discountPolicyJpaRepository.findById(discount.getId()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 할인 정책입니다."));
        target.updatePolicy(discount.getName(), discount.getDiscountPolicy().getDiscountMethod(), discount.getDiscountPolicy().getDiscountAmount());

        discountConditionJpaRepository.deleteAllByDiscountPolicy(target);
        List<DiscountConditionEntity> conditions = toConditionEntitiyList(discount, target.getId());
        List<DiscountConditionEntity> targetConditions = discountConditionJpaRepository.saveAll(conditions);

        //update
        return toDomain(target, targetConditions);
    }

    @Override
    public void deleteById(Long id) {
        DiscountPolicyEntity policy = discountPolicyJpaRepository.findById(id).orElseThrow(()->new IllegalArgumentException("존재하지 않는 할인 정책입니다."));
        discountConditionJpaRepository.deleteAllByDiscountPolicy(policy);
        discountPolicyJpaRepository.deleteById(policy.getId());

    }

    @Override
    public Optional<Discount> findById(Long id) {
        DiscountPolicyEntity policy = discountPolicyJpaRepository.findById(id).orElse(null);
        if(policy == null){
            return Optional.empty();
        }
        List<DiscountConditionEntity> conditions = discountConditionJpaRepository.findAllByDiscountPolicy(policy);
        return Optional.of(toDomain(policy, conditions));
    }

    @Override
    public List<Discount> findAllByConditions(List<DiscountCondition> conditions) {
//        Map<Long, List<DiscountConditionEntity>> policyConditionMap = new HashMap<>();
//        List<Long> policyIds = new ArrayList<>();
//        conditions.forEach(condition -> {
//            List<DiscountConditionEntity> list = discountConditionJpaRepository.findByConditionAndDiscountReason(condition.getCondition(), condition.getDiscountReason());
//            if(list.isEmpty()) {
//                return;
//            }
//
//            list.forEach(entity -> {
//                if(policyConditionMap.containsKey(entity.getDiscountPolicy().getId())){
//                    List<DiscountConditionEntity> exList = policyConditionMap.get(entity.getDiscountPolicy().getId());
//                    exList.add(entity);
//                    policyConditionMap.put(entity.getDiscountPolicy().getId(), exList);
//                }else{
//                    List<DiscountConditionEntity> init = new ArrayList<>();
//                    init.add(entity);
//                    policyConditionMap.put(entity.getDiscountPolicy().getId(), init);
//                }
//                policyIds.add(entity.getDiscountPolicy().getId());
//            });
//
//        });
//
//        if(policyIds.isEmpty()) return new ArrayList<>();
//        List<DiscountPolicyEntity> policies =  discountPolicyJpaRepository.findAllById(policyIds);
//
//        List<Discount> result = new ArrayList<>();
//        policies.forEach(
//                policy -> {
//                    result.add(toDomain(policy, policyConditionMap.get(policy.getId())));
//                }
//        );
//        return result;

        // (DiscountReason, Condition) 쌍을 생성
        List<DiscountConditionValue> values = conditions.stream()
                .map(c -> new DiscountConditionValue(c.getDiscountReason(), c.getCondition()))
                .toList();

        List<DiscountConditionEntity> conditionEntities = discountConditionJpaRepository.findAllByValues(values);
        List<Long> policyIds = conditionEntities.stream().map(entity -> entity.getDiscountPolicy().getId()).distinct().toList();

        List<DiscountPolicyEntity> policyEntities = discountPolicyJpaRepository.findAllById(policyIds);
        //안정성을 위해 각 discount policy에 종속된 모든 condition 조회
        List<DiscountConditionEntity> conditionList = discountConditionJpaRepository.findAllByDiscountPolicyIn(policyEntities);
        
        Map<Long, List<DiscountConditionEntity>> policyConditionMap = conditionList.stream().collect(Collectors.groupingBy(
                condition -> condition.getDiscountPolicy().getId()
        ));

        return policyEntities.stream().map(
                policy -> toDomain(policy, policyConditionMap.get(policy.getId()))
        ).collect(Collectors.toList());

    }

    private Discount toDomain(DiscountPolicyEntity discountPolicy, List<DiscountConditionEntity> conditions){
        return new Discount(
                discountPolicy.getId(),
                discountPolicy.getName(),
                new DiscountPolicy(discountPolicy.getPolicyValue().getDiscountMethod(), discountPolicy.getPolicyValue().getDiscountAmount()),
                conditions.stream().map(condition -> new DiscountCondition(condition.getConditionValue().getDiscountReason(), condition.getConditionValue().getCondition()))
                        .collect(Collectors.toList())
        );
    }

    private DiscountPolicyEntity toPolicyEntity(Discount discount){
        return new DiscountPolicyEntity(discount.getId(), discount.getName(), discount.getDiscountPolicy().getDiscountMethod(), discount.getDiscountPolicy().getDiscountAmount());
    }

    private List<DiscountConditionEntity> toConditionEntitiyList(Discount discount, Long policyId){
        DiscountPolicyEntity proxy = discountPolicyJpaRepository.getReferenceById(policyId);
        return discount.getConditions().stream().map(
                condition -> new DiscountConditionEntity(
                        null,
                        proxy,
                        new DiscountConditionValue(condition.getDiscountReason(), condition.getCondition())
                )
        ).collect(Collectors.toList());
    }
}
