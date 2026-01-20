package prac.backendassingment.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.backendassingment.global.enums.MemberRank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
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
