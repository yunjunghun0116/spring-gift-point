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
@Table(name = "wish_product")
@Getter
@SQLDelete(sql = "update wish_product set deleted = true where id = ?")
@SQLRestriction("deleted is false")
public class WishProduct extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;
    @NotNull
    @Column(name = "deleted")
    @Getter(AccessLevel.NONE)
    private Boolean deleted = Boolean.FALSE;

    protected WishProduct() {
    }

    public WishProduct(Product product, Member member) {
        this.product = product;
        this.member = member;
    }

    public Product getProduct() {
        return product;
    }

    public Member getMember() {
        return member;
    }
}
