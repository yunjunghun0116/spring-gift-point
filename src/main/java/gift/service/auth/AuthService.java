package gift.service.auth;

import gift.dto.auth.AuthResponse;
import gift.dto.auth.LoginRequest;
import gift.dto.auth.RegisterRequest;
import gift.dto.kakao.KakaoAuthInformation;
import gift.exception.DuplicatedEmailException;
import gift.exception.InvalidLoginInfoException;
import gift.exception.NotFoundElementException;
import gift.model.Member;
import gift.model.OauthType;
import gift.repository.MemberRepository;
import gift.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final KakaoService kakaoService;
    private final JwtProvider jwtProvider;

    public AuthResponse register(RegisterRequest registerRequest) {
        var member = saveMemberWithMemberRequest(registerRequest);
        return createAuthResponseWithMember(member);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        var member = memberRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new InvalidLoginInfoException(loginRequest.email() + "를 가진 이용자가 존재하지 않습니다."));
        member.passwordCheck(loginRequest.password());
        return createAuthResponseWithMember(member);
    }

    public AuthResponse loginWithKakaoAuth(String code) {
        var kakaoTokenResponse = kakaoService.getKakaoTokenResponse(code);
        var kakaoAuthInformation = kakaoService.getKakaoAuthInformation(kakaoTokenResponse);
        var member = getMemberWithKakaoAuth(kakaoAuthInformation);
        kakaoService.saveKakaoToken(member, kakaoTokenResponse);
        return createAuthResponseWithMember(member);
    }

    private AuthResponse createAuthResponseWithMember(Member member) {
        return AuthResponse.of(jwtProvider.generateToken(member));
    }

    private void emailValidation(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException("이미 존재하는 이메일입니다.");
        }
    }

    private Member saveMemberWithMemberRequest(RegisterRequest registerRequest) {
        emailValidation(registerRequest.email());
        var member = new Member(registerRequest.name(), registerRequest.email(), registerRequest.password());
        return memberRepository.save(member);
    }

    private Member saveMemberWithKakaoAuth(KakaoAuthInformation kakaoAuthInformation) {
        var member = new Member(kakaoAuthInformation.name(), kakaoAuthInformation.email(), OauthType.KAKAO);
        return memberRepository.save(member);
    }

    private Member getMemberWithKakaoAuth(KakaoAuthInformation kakaoAuthInformation) {
        if (!memberRepository.existsByEmail(kakaoAuthInformation.email())) {
            return saveMemberWithKakaoAuth(kakaoAuthInformation);
        }
        if (!memberRepository.existsByEmailAndPassword(kakaoAuthInformation.email(), OauthType.KAKAO.name())) {
            throw new DuplicatedEmailException("이미 존재하는 이메일입니다.");
        }
        return memberRepository.findByEmail(kakaoAuthInformation.email())
                .orElseThrow(() -> new NotFoundElementException(kakaoAuthInformation.email() + "을 가진 이용자가 존재하지 않습니다."));
    }
}
