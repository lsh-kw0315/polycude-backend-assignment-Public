package prac.backendassingment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prac.backendassingment.application.dto.MemberJoinRequest;
import prac.backendassingment.application.dto.MemberUpdateRequest;
import prac.backendassingment.domain.model.Member;
import prac.backendassingment.domain.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member joinMember(MemberJoinRequest request){
        Member member = new Member(request.getRank());
        return memberRepository.create(member);
    }

    public Member modifyMember(MemberUpdateRequest request){
        Member member = new Member(request.getId(), request.getRank());
        return memberRepository.create(member);
    }

    public Member findMemberById(Long id){
        return memberRepository.findById(id).orElse(null);
    }
}
