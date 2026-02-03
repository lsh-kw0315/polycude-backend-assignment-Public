package prac.backendassingment.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import prac.backendassingment.domain.model.Member;
import prac.backendassingment.domain.repository.MemberRepository;
import prac.backendassingment.infra.persistence.entity.MemberEntity;
import prac.backendassingment.infra.persistence.repository.MemberJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member create(Member member) {
        MemberEntity saved = memberJpaRepository.save(toEntity(member));
        return toDomain(saved);
    }



    @Override
    public Optional<Member> findById(Long id) {
        Optional<MemberEntity> member = memberJpaRepository.findById(id);
        return member.map(this::toDomain);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        Optional<MemberEntity> member = memberJpaRepository.findByLoginId(loginId);
        return member.map(this::toDomain);
    }

    @Override
    public Optional<Member> findByUsername(String username) {
        Optional<MemberEntity> member = memberJpaRepository.findByUsername(username);
        return member.map(this::toDomain);
    }

    @Override
    public Member changeMember(Member member) {
        MemberEntity target = memberJpaRepository.findById(member.getId()).orElseThrow(()->new IllegalArgumentException("유저가 존재하지 않습니다."));
        target.changeRank(member.getRank());
        target.changeProfile(member.getProfileUrl());
        target.changeUsername(member.getUsername());
        target.changePassword(member.getEncodedPassword());
        return toDomain(target);
    }

    private Member toDomain(MemberEntity memberEntity){
        return new Member(
                memberEntity.getId(),
                memberEntity.getRank(),
                memberEntity.getLoginId(),
                memberEntity.getPassword(),
                memberEntity.getUsername(),
                memberEntity.getMemberRole(),
                memberEntity.getProfileUrl()
        );
    }


    private MemberEntity toEntity(Member member){
        return new MemberEntity(
                member.getId(),
                member.getRank(),
                member.getLoginId(),
                member.getEncodedPassword(),
                member.getUsername(),
                member.getMemberRole(),
                member.getProfileUrl()
        );
    }


}
