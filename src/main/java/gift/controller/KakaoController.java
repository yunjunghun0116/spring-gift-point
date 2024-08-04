package gift.controller;

import gift.controller.api.KakaoApi;
import gift.dto.kakao.KakaoAuthRequest;
import gift.service.KakaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakao")
public class KakaoController implements KakaoApi {

    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @PostMapping("/token")
    public ResponseEntity<Void> setToken(@RequestBody KakaoAuthRequest request, @RequestAttribute("memberId") Long memberId) {
        kakaoService.saveKakaoToken(memberId, request.code());
        return ResponseEntity.noContent().build();
    }
}
