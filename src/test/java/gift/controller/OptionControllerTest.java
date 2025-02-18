package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.auth.LoginRequest;
import gift.dto.option.OptionRequest;
import gift.exception.ExceptionResponse;
import gift.service.OptionService;
import gift.service.auth.AuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OptionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OptionService optionService;
    @Autowired
    private AuthService authService;
    private String memberToken;

    @BeforeEach
    @DisplayName("이용자의 토큰 값 세팅하기")
    void setBaseData() {
        var loginRequest = new LoginRequest("member@naver.com", "password");
        memberToken = authService.login(loginRequest).token();
    }

    @Test
    @DisplayName("잘못된 수량으로 된 오류 상품 옵션 생성하기")
    void failAddOptionWithWrongQuantity() throws Exception {
        //given
        var postRequest = post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new OptionRequest("기본", 0)));
        //when
        var result = mockMvc.perform(postRequest).andReturn();
        //then
        var response = getResponseMessage(result);
        Assertions.assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Assertions.assertThat(response.message()).isEqualTo("수량은 최소 1개 이상, 1억개 미만입니다.");
    }

    @Test
    @DisplayName("빈 이름을 가진 오류 상품 옵션 생성하기")
    void failAddOptionWithEmptyName() throws Exception {
        //given
        var postRequest = post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new OptionRequest("", 1000)));
        //when
        var result = mockMvc.perform(postRequest).andReturn();
        //then
        var response = getResponseMessage(result);
        Assertions.assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Assertions.assertThat(response.message()).isEqualTo("이름의 길이는 최소 1자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("이름의 길이가 50초과인 오류 상품 생성하기")
    void failAddOptionWithNameOverLength() throws Exception {
        //given
        var postRequest = post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new OptionRequest("aaaaaaaaaaaaaaaaaabbbbbbbbbbbbcccccccccccccccddddddddddddddddddddwwwwwwwwwwqqqqqqqqqqqqqqq", 1000)));
        //when
        var result = mockMvc.perform(postRequest).andReturn();
        //then
        var response = getResponseMessage(result);
        Assertions.assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Assertions.assertThat(response.message()).isEqualTo("이름의 길이는 50자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("정상 상품 옵션 생성하기")
    void successAddOption() throws Exception {
        //given
        var postRequest = post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new OptionRequest("Large", 1500)));
        //when
        var result = mockMvc.perform(postRequest);
        //then
        var createdResult = result.andExpect(status().isCreated()).andReturn();

        deleteOptionWithCreatedHeader(createdResult);
    }

    @Test
    @DisplayName("존재하지 않는 상품에 대한 옵션 생성하기")
    void failAddOptionWithNotExistProductId() throws Exception {
        //given
        var postRequest = post("/api/products/1000/options")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new OptionRequest("Large", 1500)));
        //when
        var result = mockMvc.perform(postRequest).andReturn();
        //then
        var response = getResponseMessage(result);
        Assertions.assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("정상 옵션 생성하기 - 특수문자 포함")
    void successAddOptionWithSpecialChar() throws Exception {
        //given
        var postRequest = post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new OptionRequest("햄버거()[]+-&/_", 1000)));
        //when
        var result = mockMvc.perform(postRequest);
        //then
        var createdResult = result.andExpect(status().isCreated()).andReturn();

        deleteOptionWithCreatedHeader(createdResult);
    }

    @Test
    @DisplayName("정상 옵션 생성하기 - 공백 포함")
    void successAddOptionWithEmptySpace() throws Exception {
        //given
        var postRequest = post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new OptionRequest("햄버거 햄버거 햄버거", 1000)));
        //when
        var result = mockMvc.perform(postRequest);
        //then
        var createdResult = result.andExpect(status().isCreated()).andReturn();

        deleteOptionWithCreatedHeader(createdResult);
    }

    @Test
    @DisplayName("오류 상품 생성하기 - 허용되지 않은 특수문자 포함")
    void failAddOptionWithSpecialChar() throws Exception {
        //given
        var postRequest = post("/api/products/1/options")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new OptionRequest("햄버거()[]+-&/_**", 1000)));
        //when
        var result = mockMvc.perform(postRequest).andReturn();
        //then
        var response = getResponseMessage(result);
        Assertions.assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        Assertions.assertThat(response.message()).isEqualTo("허용되지 않은 형식의 이름입니다.");
    }

    private void deleteOptionWithCreatedHeader(MvcResult mvcResult) {
        var location = mvcResult.getResponse().getHeader("Location");
        var splitResult = location.split("/");
        var productId = Long.parseLong(splitResult[splitResult.length - 3]);
        var optionId = Long.parseLong(splitResult[splitResult.length - 1]);
        optionService.deleteOption(productId, optionId);
    }

    private ExceptionResponse getResponseMessage(MvcResult result) throws Exception {
        var resultString = result.getResponse().getContentAsString();
        return objectMapper.readValue(resultString, ExceptionResponse.class);
    }
}
