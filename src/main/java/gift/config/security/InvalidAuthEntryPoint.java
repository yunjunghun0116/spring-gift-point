package gift.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class InvalidAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        var exceptionMessage = (String) request.getAttribute("exception");
        var exceptionResponse = new ExceptionResponse(HttpStatus.UNAUTHORIZED.value(), exceptionMessage);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        objectMapper.writeValue(response.getWriter(), exceptionResponse);
    }
}
