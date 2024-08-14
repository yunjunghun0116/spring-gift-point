package gift.config.security;

import gift.exception.NotFoundElementException;
import gift.repository.MemberRepository;
import gift.service.auth.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (canSkipFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = getTokenWithAuthorizationHeader(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        var memberId = getMemberIdWithToken(request, token);
        if (memberId == null) {
            filterChain.doFilter(request, response);
            return;
        }
        setMemberAuthToken(request, memberId);

        filterChain.doFilter(request, response);
    }

    private void setMemberAuthToken(HttpServletRequest request, Long memberId) {
        try {
            var member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NotFoundElementException("존재하지 않는 이용자 입니다."));
            var authorities = List.of(new SimpleGrantedAuthority(member.getMemberRole().name()));
            var authToken = new UsernamePasswordAuthenticationToken(member.getId(), member.getEmail(), authorities);
            var authDetails = new WebAuthenticationDetailsSource().buildDetails(request);
            authToken.setDetails(authDetails);

            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception exception) {
            request.setAttribute("exception", "토큰이 유효하지 않습니다.");
        }
    }

    private Long getMemberIdWithToken(HttpServletRequest request, String token) {
        try {
            return jwtProvider.getMemberIdWithToken(token);
        } catch (Exception exception) {
            request.setAttribute("exception", "토큰이 만료되었습니다.");
            return null;
        }
    }

    private String getTokenWithAuthorizationHeader(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            request.setAttribute("exception", "잘못된 토큰 정보입니다.");
            return null;
        }
        var header = authorizationHeader.split(" ");
        if (header.length != 2) {
            request.setAttribute("exception", "잘못된 토큰 정보입니다.");
            return null;
        }
        return header[1];
    }

    private boolean canSkipFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        var skipUris = List.of("/swagger-ui", "/swagger-resources", "/v3/api-docs", "/api/members/login", "/api/members/register");
        var uri = request.getRequestURI();
        for (var skipUri : skipUris) {
            if (uri.startsWith(skipUri)) {
                return true;
            }
        }
        return false;
    }
}
