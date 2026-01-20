package prac.backendassingment.domain.model;

import lombok.*;
import prac.backendassingment.global.enums.MemberRank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Member {
    private Long id;
    private MemberRank rank;

    public Member(MemberRank rank){
        this(null, rank);
    }

    public Member(Long id, MemberRank rank){
        this.id = id;
        this.rank = rank != null ? rank : MemberRank.NORMAL;
    }

    public void changeRank(MemberRank rank){
        this.rank = rank != null ? rank : this.rank;
    }
}
