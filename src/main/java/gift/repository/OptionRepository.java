package gift.repository;

import gift.model.Option;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findAllByProductId(Long productId);

    boolean existsOptionByProductIdAndName(Long productId, String name);

    void deleteAllByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select o from Option o where o.id = :id")
    Optional<Option> findByIdWithLock(Long id);
}
