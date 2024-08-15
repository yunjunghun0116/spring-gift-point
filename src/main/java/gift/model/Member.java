package gift.model;

import gift.exception.InvalidLoginInfoException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "member")
@Getter
@SQLDelete(sql = "update member set deleted = true where id = ?")
@SQLRestriction("deleted is false")
public class Member extends BaseEntity {
    @NotNull
    @Column(name = "email", unique = true)
    private String email;
    @NotNull
    @Column(name = "password")
    private String password;
    @NotNull
    @Column(name = "name")
    private String name;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "member_role")
    private MemberRole memberRole = MemberRole.MEMBER;
    @NotNull
    @Column(name = "deleted")
    private Boolean deleted = Boolean.FALSE;

    protected Member() {
    }

    public Member(String name, String email, OauthType oauthType) {
        this.name = name;
        this.email = email;
        this.password = oauthType.name();
    }

    public Member(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void passwordCheck(String inputPassword) {
        if (!password.equals(inputPassword)) {
            throw new InvalidLoginInfoException("로그인 정보가 유효하지 않습니다.");
        }
    }
}
