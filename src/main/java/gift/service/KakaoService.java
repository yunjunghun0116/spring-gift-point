package gift.service;

import gift.client.KakaoApiClient;
import gift.config.properties.KakaoProperties;
import gift.dto.giftorder.GiftOrderResponse;
import gift.dto.kakao.KakaoAuthInformation;
import gift.dto.kakao.KakaoTokenResponse;
import gift.exception.NotFoundElementException;
import gift.exception.UnauthorizedAccessException;
import gift.model.Member;
import gift.model.OauthToken;
import gift.model.OauthType;
import gift.repository.MemberRepository;
import gift.repository.OauthTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class KakaoService {

    private final MemberRepository memberRepository;
    private final OauthTokenRepository oauthTokenRepository;
    private final KakaoApiClient kakaoApiClient;
    private final KakaoProperties kakaoProperties;

    public KakaoTokenResponse getKakaoTokenResponse(String code) {
        return kakaoApiClient.getTokenResponse(code, kakaoProperties.redirectUri());
    }

    public void saveKakaoToken(Long memberId, String code) {
        var kakaoTokenResponse = kakaoApiClient.getTokenResponse(code, kakaoProperties.tokenUri());
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(memberId + "를 가진 이용자가 존재하지 않습니다."));
        saveKakaoToken(member, kakaoTokenResponse);
    }

    public OauthToken saveKakaoToken(Member member, KakaoTokenResponse kakaoTokenResponse) {
        if (oauthTokenRepository.existsByMemberIdAndOauthType(member.getId(), OauthType.KAKAO)) {
            return updateOauthToken(member, kakaoTokenResponse);
        }
        return createAndSaveOauthToken(member, kakaoTokenResponse);
    }

    public KakaoAuthInformation getKakaoAuthInformation(KakaoTokenResponse kakaoTokenResponse) {
        var response = kakaoApiClient.getKakaoAuthResponse(kakaoTokenResponse);
        var kakaoAccount = response.kakaoAccount();
        var name = kakaoAccount.profile().name();
        var email = kakaoAccount.email();
        return KakaoAuthInformation.of(name, email);
    }

    public void sendOrderResponseWithKakaoMessage(Long memberId, GiftOrderResponse giftOrderResponse) {
        var kakaoToken = oauthTokenRepository.findByMemberIdAndOauthType(memberId, OauthType.KAKAO)
                .orElseThrow(() -> new NotFoundElementException(memberId + "를 가진 이용자의 카카오 토큰 정보가 존재하지 않습니다."));
        var validatedKakaoToken = tokenValidation(kakaoToken);
        kakaoApiClient.sendSelfMessageOrder(validatedKakaoToken.getAccessToken(), giftOrderResponse);
    }

    public void deleteByMemberId(Long memberId) {
        if (!oauthTokenRepository.existsByMemberIdAndOauthType(memberId, OauthType.KAKAO)) return;
        oauthTokenRepository.deleteAllByMemberId(memberId);
    }

    private OauthToken tokenValidation(OauthToken oauthToken) {
        if (!oauthToken.canUseRefreshToken()) {
            throw new UnauthorizedAccessException("유효하지 않은 카카오 토큰입니다. 갱신이 필요합니다.");
        }
        if (!oauthToken.canUseAccessToken()) {
            var kakaoTokenResponse = kakaoApiClient.getRefreshedTokenResponse(oauthToken.getRefreshToken());
            oauthToken = saveKakaoToken(oauthToken.getMember(), kakaoTokenResponse);
        }
        return oauthToken;
    }

    private OauthToken createAndSaveOauthToken(Member member, KakaoTokenResponse kakaoTokenResponse) {
        var kakaoToken = new OauthToken(member, OauthType.KAKAO, kakaoTokenResponse.accessToken(), kakaoTokenResponse.accessTokenExpiresIn(), kakaoTokenResponse.refreshToken(), kakaoTokenResponse.refreshTokenExpiresIn());
        return oauthTokenRepository.save(kakaoToken);
    }

    private OauthToken updateOauthToken(Member member, KakaoTokenResponse kakaoTokenResponse) {
        var kakaoToken = oauthTokenRepository.findByMemberIdAndOauthType(member.getId(), OauthType.KAKAO)
                .orElseThrow(() -> new NotFoundElementException(member.getId() + "를 가진 이용자의 카카오 토큰 정보가 존재하지 않습니다."));
        kakaoToken.updateToken(kakaoTokenResponse.accessToken(), kakaoTokenResponse.accessTokenExpiresIn(), kakaoTokenResponse.refreshToken(), kakaoTokenResponse.refreshTokenExpiresIn());
        return oauthTokenRepository.save(kakaoToken);
    }
}
