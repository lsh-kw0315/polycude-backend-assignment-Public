package prac.backendassingment.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prac.backendassingment.infra.persistence.entity.MemberEntity;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
}
