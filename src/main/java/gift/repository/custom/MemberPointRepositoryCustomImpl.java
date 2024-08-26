package gift.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import gift.model.QMemberPoint;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberPointRepositoryCustomImpl implements MemberPointRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Integer findPointByMemberId(Long memberId) {
        QMemberPoint memberPoint = QMemberPoint.memberPoint;
        var memberPoints = jpaQueryFactory.select(memberPoint.point.sum())
                .from(memberPoint)
                .where(memberPoint.member.id.eq(memberId))
                .fetchOne();
        if (memberPoints == null) {
            return 0;
        }
        return memberPoints;
    }
}
