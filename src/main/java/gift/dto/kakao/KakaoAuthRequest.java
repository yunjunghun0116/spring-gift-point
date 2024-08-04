package gift.dto.kakao;

import jakarta.validation.constraints.NotBlank;

public record KakaoAuthRequest(
        @NotBlank(message = "인가코드는 빈값이면 안됩니다.")
        String code
) {
}
