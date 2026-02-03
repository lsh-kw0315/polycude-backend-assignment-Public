package prac.backendassingment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.global.enums.MemberRank;
import prac.backendassingment.global.enums.MemberRole;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRank rank;

    @Column(unique = true, nullable = false)
    private String loginId;
    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String username;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;
    private String profileUrl;

    public void changeRank(MemberRank rank){
        this.rank = rank;
    }
    public void changeUsername(String username) { this.username = username;}
    public void changePassword(String password) { this.password = password;}
    public void changeProfile(String profileUrl) { this.profileUrl = profileUrl;}

}
