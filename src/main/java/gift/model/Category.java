package gift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "category")
@Getter
@SQLDelete(sql = "update category set deleted = true where id = ?")
@SQLRestriction("deleted is false")
public class Category extends BaseEntity {
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Column(name = "description")
    private String description;
    @NotNull
    @Column(name = "color")
    private String color;
    @NotNull
    @Column(name = "image_url")
    private String imageUrl;
    @NotNull
    @Column(name = "deleted")
    @Getter(AccessLevel.NONE)
    private Boolean deleted = Boolean.FALSE;

    protected Category() {
    }

    public Category(String name, String description, String color, String imageUrl) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.imageUrl = imageUrl;
    }

    public void updateCategory(String name, String description, String color, String imageUrl) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.imageUrl = imageUrl;
    }
}
