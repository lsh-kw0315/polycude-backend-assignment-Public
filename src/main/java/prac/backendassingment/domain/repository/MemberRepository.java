package prac.backendassingment.domain.repository;

import prac.backendassingment.application.dto.MemberUpdateRequest;
import prac.backendassingment.domain.model.Member;

import java.util.Optional;

public interface MemberRepository {

    Member create(Member member);
    Optional<Member> findById(Long id);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByUsername(String username);

    Member changeMember(Member member);
}
