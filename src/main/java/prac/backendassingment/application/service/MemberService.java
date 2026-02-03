package prac.backendassingment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.backendassingment.application.dto.LoginRequest;
import prac.backendassingment.application.dto.MemberJoinRequest;
import prac.backendassingment.application.dto.MemberUpdateRequest;
import prac.backendassingment.domain.model.Member;
import prac.backendassingment.domain.repository.MemberRepository;
import prac.backendassingment.global.enums.MemberRole;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member joinMember(MemberJoinRequest request){
        Optional<Member> loginIdExists = memberRepository.findByLoginId(request.getLoginId());
        if(loginIdExists.isPresent()) throw new IllegalStateException("중복되는 아이디 혹은 닉네임입니다.");

        Optional<Member> usernameExists = memberRepository.findByUsername(request.getUsername());
        if(usernameExists.isPresent()) throw new IllegalStateException("중복되는 아이디 혹은 닉네임입니다.");

        String encoded = passwordEncoder.encode(request.getPassword());
        Member member = new Member(request.getLoginId(), encoded, request.getUsername(), MemberRole.USER, request.getProfileUrl());
        return memberRepository.create(member);
    }

    @Transactional
    public Member modifyMember(MemberUpdateRequest request){
        Member member = memberRepository.findById(request.getId()).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저"));

        member.changeProfile(request.getProfileUrl());
        member.changeUsername(request.getUsername());
        member.changePassword(request.getPassword());

        return memberRepository.changeMember(member);
    }

    public Member findMemberById(Long id){
        return memberRepository.findById(id).orElse(null);
    }

    public Member login(LoginRequest request) {
        Member member = memberRepository.findByLoginId(request.getLoginId()).orElseThrow(() -> new IllegalArgumentException("ID 혹은 비밀번호가 올바르지 않음."));
        if(!passwordEncoder.matches(request.getPassword(), member.getEncodedPassword())) throw new IllegalStateException("ID 혹은 비밀번호가 올바르지 않음.");

        return member;

    }
}
