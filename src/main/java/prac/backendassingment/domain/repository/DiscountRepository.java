package prac.backendassingment.domain.repository;

import prac.backendassingment.domain.model.Discount;
import prac.backendassingment.domain.model.DiscountCondition;

import java.util.List;
import java.util.Optional;

public interface DiscountRepository {
    Discount save(Discount discount);
    void deleteById(Long id);
    Optional<Discount> findById(Long id);
    List<Discount> findAllByConditions(List<DiscountCondition> conditions);

}
