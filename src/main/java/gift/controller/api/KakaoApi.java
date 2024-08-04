package gift.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증 토큰 API")
public interface KakaoApi {

    @Operation(summary = "카카오 로그인을 통해 회원을 인증하고 토큰을 등록한다.", description = "코드를 얻기 위한 Redirect URI : http://localhost:3000/api/kakao/token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "토큰 등록 성공"),
            @ApiResponse(responseCode = "401", description = "허용되지 않는 요청"),
            @ApiResponse(responseCode = "404", description = "토큰 등록 실패(사유 : 존재하지 않는 이용자 정보입니다.)"),
            @ApiResponse(responseCode = "500", description = "내부 서버의 오류")
    })
    ResponseEntity<Void> setToken(String code, Long memberId);
}
