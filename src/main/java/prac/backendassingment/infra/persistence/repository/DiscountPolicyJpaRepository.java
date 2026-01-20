package prac.backendassingment.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prac.backendassingment.infra.persistence.entity.DiscountPolicyEntity;

public interface DiscountPolicyJpaRepository extends JpaRepository<DiscountPolicyEntity, Long> {

}
