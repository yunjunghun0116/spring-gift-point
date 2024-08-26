package gift.repository;

import gift.model.MemberPoint;
import gift.repository.custom.MemberPointRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPointRepository extends JpaRepository<MemberPoint, Long>, MemberPointRepositoryCustom {
}
