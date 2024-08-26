package gift.repository;

import gift.model.Member;
import gift.model.Product;
import gift.model.WishProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishProductRepository extends JpaRepository<WishProduct, Long> {
    boolean existsByProductAndMember(Product product, Member member);

    List<WishProduct> findAllByMemberId(Long memberId, Pageable pageable);

    void deleteAllByProductId(Long id);

    void deleteAllByMemberId(Long id);

    void deleteAllByMemberIdAndProductId(Long memberId, Long productId);
}
