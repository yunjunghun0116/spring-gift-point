package gift.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String NOT_FOUND_MESSAGE = "존재하지 않는 리소스에 대한 접근입니다.";
    private static final String INVALID_PRODUCT_NAME_WITH_KAKAO_MESSAGE = "카카오가 포함된 문구는 담당 MD와 협의한 경우에만 사용할 수 있습니다.";
    private static final String DUPLICATED_EMAIL_MESSAGE = "이미 존재하는 이메일입니다.";
    private static final String DUPLICATED_NAME_MESSAGE = "이미 존재하는 이름입니다.";
    private static final String INVALID_LOGIN_INFO_MESSAGE = "로그인 정보가 유효하지 않습니다.";
    private static final String INVALID_PAGE_REQUEST_MESSAGE = "요청에 담긴 페이지 정보가 유효하지 않습니다.";
    private static final String EXPIRED_JWT_MESSAGE = "인증 정보가 만료되었습니다.";

    @ExceptionHandler(value = NotFoundElementException.class)
    public ResponseEntity<ExceptionResponse> notFoundElementExceptionHandling() {
        return getExceptionResponse(NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidProductNameWithKAKAOException.class)
    public ResponseEntity<ExceptionResponse> invalidProductNameWithKAKAOExceptionHandling() {
        return getExceptionResponse(INVALID_PRODUCT_NAME_WITH_KAKAO_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DuplicatedEmailException.class)
    public ResponseEntity<ExceptionResponse> duplicatedEmailExceptionHandling() {
        return getExceptionResponse(DUPLICATED_EMAIL_MESSAGE, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = DuplicatedNameException.class)
    public ResponseEntity<ExceptionResponse> duplicatedNameExceptionHandling() {
        return getExceptionResponse(DUPLICATED_NAME_MESSAGE, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InvalidLoginInfoException.class)
    public ResponseEntity<ExceptionResponse> invalidLoginInfoExceptionHandling() {
        return getExceptionResponse(INVALID_LOGIN_INFO_MESSAGE, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = GiftOrderException.class)
    public ResponseEntity<ExceptionResponse> giftOrderExceptionHandling(GiftOrderException exception) {
        return getExceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnauthorizedAccessException.class)
    public ResponseEntity<ExceptionResponse> unauthorizedAccessExceptionHandling(UnauthorizedAccessException exception) {
        return getExceptionResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> alreadyExistsExceptionHandling(AlreadyExistsException exception) {
        return getExceptionResponse(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> expiredJwtExceptionHandling() {
        return getExceptionResponse(EXPIRED_JWT_MESSAGE, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ExceptionResponse> badRequestExceptionHandling(BadRequestException exception) {
        return getExceptionResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentNotValidExceptionHandling(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getDefaultMessage());
        }

        return getExceptionResponse(builder.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PropertyReferenceException.class)
    public ResponseEntity<ExceptionResponse> propertyReferenceExceptionHandling() {
        return getExceptionResponse(INVALID_PAGE_REQUEST_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> internalServerExceptionHandling(Exception exception) {
        return getExceptionResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> getExceptionResponse(String message, HttpStatus status) {
        var response = new ExceptionResponse(status.value(), message);
        return ResponseEntity.status(status).body(response);
    }
}
