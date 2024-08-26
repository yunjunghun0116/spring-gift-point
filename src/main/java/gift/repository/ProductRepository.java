package gift.repository;

import gift.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategoryId(Long categoryId);

    List<Product> findAllByCategoryId(Long categoryId, Pageable pageable);
}
