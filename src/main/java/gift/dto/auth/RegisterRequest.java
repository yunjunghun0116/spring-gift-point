package gift.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "이름의 길이는 최소 1자 이상이어야 합니다.")
        String name,
        @Pattern(regexp = "^[0-9a-z\\-\\_\\+\\w]*@([0-9a-z]+\\.)+[a-z]{2,9}", message = "허용되지 않은 형식의 이메일입니다.")
        String email,
        @Pattern(regexp = "^[0-9a-zA-Z\\-\\_\\+\\!\\*\\@\\#\\$\\%\\^\\&\\(\\)\\.]{8,}$", message = "허용되지 않은 형식의 패스워드입니다.")
        String password
) {
}
