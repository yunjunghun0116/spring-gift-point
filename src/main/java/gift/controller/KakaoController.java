package gift.controller;

import gift.controller.api.KakaoApi;
import gift.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kakao")
@RequiredArgsConstructor
public class KakaoController implements KakaoApi {

    private final KakaoService kakaoService;

    @GetMapping("/token")
    public ResponseEntity<Void> setToken(@RequestParam String code) {
        var memberId = getMemberId();
        kakaoService.saveKakaoToken(memberId, code);
        return ResponseEntity.noContent().build();
    }

    private Long getMemberId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal().toString();
        return Long.parseLong(principal);
    }
}
