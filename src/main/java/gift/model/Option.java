package gift.model;

import gift.exception.BadRequestException;
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
@Table(name = "option")
@Getter
@SQLDelete(sql = "update option set deleted = true where id = ?")
@SQLRestriction("deleted is false")
public class Option extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "quantity")
    private Integer quantity;
    @NotNull
    @Column(name = "deleted")
    @Getter(AccessLevel.NONE)
    private Boolean deleted = Boolean.FALSE;

    protected Option() {
    }

    public Option(Product product, String name, Integer quantity) {
        this.product = product;
        this.name = name;
        this.quantity = quantity;
    }

    public void updateOptionInfo(String newName, Integer newQuantity) {
        this.name = newName;
        this.quantity = newQuantity;
    }

    public void subtract(Integer subQuantity) {
        if (subQuantity > quantity) {
            throw new BadRequestException("주문량이 옵션의 잔여 갯수를 초과합니다");
        }
        this.quantity = quantity - subQuantity;
    }
}
