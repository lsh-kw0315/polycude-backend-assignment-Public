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
        if(member.getId() == null){
            MemberEntity saved = memberJpaRepository.save(toEntity(member));
            return toDomain(saved);
        }

        MemberEntity target = memberJpaRepository.findById(member.getId()).orElseThrow(()->new IllegalArgumentException("유저가 존재하지 않습니다."));
        target.changeRank(member.getRank());
        return toDomain(target);
    }

    @Override
    public Optional<Member> findById(Long id) {
        Optional<MemberEntity> member = memberJpaRepository.findById(id);
        return member.map(this::toDomain);
    }

    private Member toDomain(MemberEntity memberEntity){
        return new Member(
                memberEntity.getId(),
                memberEntity.getRank()
        );
    }

    private MemberEntity toEntity(Member member){
        return new MemberEntity(
                member.getId(),
                member.getRank()
        );
    }


}
