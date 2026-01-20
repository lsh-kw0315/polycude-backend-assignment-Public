package prac.backendassingment.domain.repository;

import prac.backendassingment.domain.model.Member;

import java.util.Optional;

public interface MemberRepository {

    Member create(Member member);
    Optional<Member> findById(Long id);
}
