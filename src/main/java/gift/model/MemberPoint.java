package gift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "member_point")
@Getter
@SQLDelete(sql = "update member_point set deleted = true where id = ?")
@SQLRestriction("deleted is false")
public class MemberPoint extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;
    @NotNull
    @Column(name = "point")
    private Integer point;
    @NotNull
    @Column(name = "deleted")
    @Getter(AccessLevel.NONE)
    private Boolean deleted = Boolean.FALSE;

    protected MemberPoint() {
    }

    public MemberPoint(Member member, Integer point) {
        this.member = member;
        this.point = point;
    }

    public void addPoint(Integer additionalPoint) {
        this.point = point + additionalPoint;
    }

    public void subtractPoint(Integer subtractPoint) {
        this.point = point - subtractPoint;
    }
}
