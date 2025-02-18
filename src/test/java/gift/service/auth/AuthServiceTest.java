package gift.service.auth;

import gift.config.properties.JwtProperties;
import gift.dto.auth.LoginRequest;
import gift.dto.auth.RegisterRequest;
import gift.dto.giftorder.GiftOrderResponse;
import gift.dto.kakao.KakaoAuthInformation;
import gift.dto.kakao.KakaoTokenResponse;
import gift.exception.DuplicatedEmailException;
import gift.exception.InvalidLoginInfoException;
import gift.model.Member;
import gift.model.OauthToken;
import gift.model.OauthType;
import gift.repository.MemberRepository;
import gift.service.KakaoService;
import gift.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
class AuthServiceTest {

    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private MemberService memberService;
    @Autowired
    private JwtProvider jwtProvider;
    private KakaoService kakaoService = Mockito.mock(KakaoService.class);

    @BeforeEach
    void mockKakaoServiceSetUp() {
        authService = new AuthService(memberRepository, kakaoService, jwtProvider);
        Mockito.doNothing().when(kakaoService).saveKakaoToken(any(Long.class), any(String.class));
        Mockito.doNothing().when(kakaoService).sendOrderResponseWithKakaoMessage(any(Long.class), any(GiftOrderResponse.class));
        Mockito.doNothing().when(kakaoService).deleteByMemberId(any(Long.class));

        var mockMember = new Member("test", "MOCK@naver.com", OauthType.KAKAO);
        var mockKakaoTokenResponse = new KakaoTokenResponse("ACCESSTOKEN", 10000, "REFRESHTOKEN", 600000);
        var mockKakaoToken = new OauthToken(mockMember, OauthType.KAKAO, "ACCESSTOKEN", 10000, "REFRESHTOKEN", 600000);
        var mockKakaoAuthInformation = new KakaoAuthInformation("MOCK", "MOCK@naver.com");

        Mockito.when(kakaoService.saveKakaoToken(any(Member.class), any(KakaoTokenResponse.class)))
                .thenReturn(mockKakaoToken);
        Mockito.when(kakaoService.getKakaoTokenResponse(any(String.class)))
                .thenReturn(mockKakaoTokenResponse);
        Mockito.when(kakaoService.getKakaoAuthInformation(any(KakaoTokenResponse.class)))
                .thenReturn(mockKakaoAuthInformation);
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시도하기 - 실패")
    void failRegisterWithDuplicatedEmail() {
        //given
        var registerRequest = new RegisterRequest("test", "test@naver.com", "testPassword");
        var auth = authService.register(registerRequest);
        var id = jwtProvider.getMemberIdWithToken(auth.token());
        //when, then
        Assertions.assertThatThrownBy(() -> authService.register(registerRequest)).isInstanceOf(DuplicatedEmailException.class);

        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("로그인 실행하기 - 성공")
    void successLogin() {
        //given
        var registerRequest = new RegisterRequest("test", "test@naver.com", "testPassword");
        var auth = authService.register(registerRequest);
        var loginRequest = new LoginRequest("test@naver.com", "testPassword");
        //when
        var loginAuth = authService.login(loginRequest);
        //then
        var id = jwtProvider.getMemberIdWithToken(auth.token());
        var loginId = jwtProvider.getMemberIdWithToken(loginAuth.token());
        Assertions.assertThat(id).isEqualTo(loginId);

        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("로그인 실행하기 - 실패")
    void failLoginWithWrongPassword() {
        //given
        var registerRequest = new RegisterRequest("test", "test@naver.com", "testPasswords");
        var auth = authService.register(registerRequest);
        var loginRequest = new LoginRequest("test@naver.com", "testPassword");
        //when, then
        Assertions.assertThatThrownBy(() -> authService.login(loginRequest)).isInstanceOf(InvalidLoginInfoException.class);

        var id = jwtProvider.getMemberIdWithToken(auth.token());
        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("카카오 회원가입하기 - 성공")
    void successKakaoRegister() {
        //given
        var code = "인가코드";
        //when
        var auth = authService.loginWithKakaoAuth(code);
        //then
        var id = jwtProvider.getMemberIdWithToken(auth.token());

        Assertions.assertThat(id).isNotNull();

        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("카카오 회원가입하기 - 실패")
    void failKakaoRegisterExistsEmail() {
        //given
        var registerRequest = new RegisterRequest("test", "MOCK@naver.com", "testPassword");
        var auth = authService.register(registerRequest);
        var code = "인가코드";
        //when, then
        Assertions.assertThatThrownBy(() -> authService.loginWithKakaoAuth(code)).isInstanceOf(DuplicatedEmailException.class);

        var id = jwtProvider.getMemberIdWithToken(auth.token());
        memberService.deleteMember(id);
    }

    @Test
    @DisplayName("이미 카카오로 가입된 이용자의 이메일로 회원가입하기 - 실패")
    void failRegisterWithAlreadyExistsKakaoEmail() {
        //given
        var code = "인가코드";
        var auth = authService.loginWithKakaoAuth(code);
        var memberId = jwtProvider.getMemberIdWithToken(auth.token());
        var registerRequest = new RegisterRequest("test", "MOCK@naver.com", "testPassword");
        //when, then
        Assertions.assertThatThrownBy(() -> authService.register(registerRequest)).isInstanceOf(DuplicatedEmailException.class);

        memberService.deleteMember(memberId);
    }

    @Test
    @DisplayName("카카오 로그인하기 - 성공")
    void successKakaoLogin() {
        //given
        var code = "인가코드";
        var auth = authService.loginWithKakaoAuth(code);
        var memberId = jwtProvider.getMemberIdWithToken(auth.token());
        //when
        var loginAuth = authService.loginWithKakaoAuth(code);
        var loginMemberId = jwtProvider.getMemberIdWithToken(loginAuth.token());

        Assertions.assertThat(memberId).isEqualTo(loginMemberId);

        memberService.deleteMember(memberId);
    }
}
