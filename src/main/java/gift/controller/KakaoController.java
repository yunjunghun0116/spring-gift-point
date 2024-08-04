package gift.controller;

import gift.controller.api.KakaoApi;
import gift.service.KakaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakao")
public class KakaoController implements KakaoApi {

    private final KakaoService kakaoService;

    public KakaoController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @GetMapping("/token")
    public ResponseEntity<Void> setToken(@RequestParam String code, @RequestAttribute("memberId") Long memberId) {
        kakaoService.saveKakaoToken(memberId, code);
        return ResponseEntity.noContent().build();
    }
}
