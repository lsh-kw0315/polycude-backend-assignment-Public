package prac.backendassingment.domain.model;

import lombok.*;
import prac.backendassingment.global.enums.MemberRank;
import prac.backendassingment.global.enums.MemberRole;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Member {
    private Long id;
    private MemberRank rank;
    private String loginId;
    private String encodedPassword;
    private String username;
    private MemberRole memberRole;
    private String profileUrl;

    public Member(String loginId ,String encodedPassword, String username, MemberRole memberRole, String profileUrl ){
        this(null, null, loginId, encodedPassword, username, memberRole, profileUrl);
    }
    public Member(MemberRank rank,String loginId ,String encodedPassword, String username, MemberRole memberRole, String profileUrl ){
        this(null, rank, loginId, encodedPassword, username, memberRole, profileUrl);
    }

    public Member(Long id, MemberRank rank, String encodedPassword, String username, String profileUrl){
        this(id, rank, null, encodedPassword, username, null, profileUrl);
    }

    public Member(Long id, MemberRank rank, String loginId ,String encodedPassword, String username, MemberRole memberRole, String profileUrl){
        this.id = id;
        this.rank = rank != null ? rank : MemberRank.NORMAL;

        if(encodedPassword == null || encodedPassword.isEmpty()) throw new IllegalArgumentException("유저 패스워드는 공백일 수 없습니다.");
        this.encodedPassword = encodedPassword;

        if(loginId == null || loginId.isEmpty()) throw new IllegalArgumentException("로그인 아이디는 공백일 수 없습니다.");
        this.loginId = loginId;

        if(username == null || username.isEmpty()) throw new IllegalArgumentException("유저 닉네임을 공백일 수 없습니다.");
        this.username = username;

        this.memberRole = memberRole != null ? memberRole : MemberRole.USER;

        this.profileUrl = profileUrl;
    }

    public void changeRank(MemberRank rank){
        this.rank = rank != null ? rank : this.rank;
    }
    public void changeUsername(String username) {this.username = username != null && !username.isEmpty() ? username : this.username;}
    public void changePassword(String encodedPassword) {this.encodedPassword = encodedPassword != null && !encodedPassword.isEmpty() ? encodedPassword : this.encodedPassword;}
    public void changeProfile(String profileUrl) { this.profileUrl = profileUrl;}
}
