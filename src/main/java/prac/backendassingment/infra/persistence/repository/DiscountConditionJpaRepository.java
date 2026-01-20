package prac.backendassingment.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prac.backendassingment.infra.persistence.entity.DiscountConditionEntity;
import prac.backendassingment.infra.persistence.entity.DiscountConditionValue;
import prac.backendassingment.infra.persistence.entity.DiscountPolicyEntity;

import java.util.List;

public interface DiscountConditionJpaRepository extends JpaRepository<DiscountConditionEntity, Long> {

    void deleteAllByDiscountPolicy(DiscountPolicyEntity policy);

    List<DiscountConditionEntity> findAllByDiscountPolicy(DiscountPolicyEntity policy);

    List<DiscountConditionEntity> findByConditionValue(DiscountConditionValue value);

    @Query("select dc from DiscountConditionEntity dc " +
            "where dc.conditionValue in :values")
    List<DiscountConditionEntity> findAllByValues(@Param("values") List<DiscountConditionValue> values);

    List<DiscountConditionEntity> findAllByDiscountPolicyIn(List<DiscountPolicyEntity> policies);
}
