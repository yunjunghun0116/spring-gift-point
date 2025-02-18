package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.auth.LoginRequest;
import gift.dto.wishproduct.WishProductRequest;
import gift.dto.wishproduct.WishProductResponse;
import gift.exception.ExceptionResponse;
import gift.service.WishProductService;
import gift.service.auth.AuthService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WishProductService wishProductService;
    @Autowired
    private AuthService authService;
    private String managerToken;
    private String memberToken;


    @BeforeEach
    @DisplayName("관리자, 이용자의 토큰 값 세팅하기")
    void setBaseData() {
        var managerLoginRequest = new LoginRequest("admin@naver.com", "password");
        managerToken = authService.login(managerLoginRequest).token();
        var memberLoginRequest = new LoginRequest("member@naver.com", "password");
        memberToken = authService.login(memberLoginRequest).token();
    }

    @Test
    @DisplayName("위시 리스트 상품 추가하기")
    void successAddWishProduct() throws Exception {
        //given
        var postRequest = post("/api/wishes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new WishProductRequest(1L)));
        //when
        var result = mockMvc.perform(postRequest);
        //then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("위시 리스트 상품 조회하기")
    void successGetWishProducts() throws Exception {
        //given
        var wishProduct = wishProductService
                .addWishProduct(new WishProductRequest(1L), 1L);
        var getRequest = get("/api/wishes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken);
        //when
        var readResult = mockMvc.perform(getRequest);
        //then
        readResult.andExpect(status().isOk());

        wishProductService.deleteWishProduct(wishProduct.id());
    }

    @Test
    @DisplayName("이용자끼리의 위시리스트가 다르다")
    void successGetDifferentWishProducts() throws Exception {
        //given
        var wishProduct1AddRequest = new WishProductRequest(1L);
        var wishProduct2AddRequest = new WishProductRequest(2L);
        wishProductService.addWishProduct(wishProduct1AddRequest, 1L);
        wishProductService.addWishProduct(wishProduct2AddRequest, 1L);
        var getRequest = get("/api/wishes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + managerToken);
        //when
        var managerReadResult = mockMvc.perform(getRequest);
        //then
        var managerWishResult = managerReadResult.andExpect(status().isOk()).andReturn();
        var managerWishLength = managerWishResult.getResponse().getContentLength();
        Assertions.assertThat(managerWishLength).isEqualTo(0);
        var memberWishProducts = wishProductService.getWishProducts(1L, PageRequest.of(0, 10));
        Assertions.assertThat(memberWishProducts.size()).isEqualTo(2);

        deleteWishProducts(memberWishProducts);
    }

    @Test
    @DisplayName("이미 존재하는 상품을 추가하면 409 예외를 반환한다.")
    void failAddWithAlreadyExistsProduct() throws Exception {
        //given
        var wishProduct = wishProductService
                .addWishProduct(new WishProductRequest(1L),
                        1L);
        var postRequest = post("/api/wishes")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken)
                .content(objectMapper.writeValueAsString(new WishProductRequest(1L)));
        //when
        var result = mockMvc.perform(postRequest).andReturn();
        //then
        var response = getResponseMessage(result);
        Assertions.assertThat(response.status()).isEqualTo(HttpStatus.CONFLICT.value());

        wishProductService.deleteWishProduct(wishProduct.id());
    }

    @Test
    @DisplayName("잘못된 정렬 데이터가 올 경우 예외를 던진다.")
    void failGetWishProductsWithInvalidPageSort() throws Exception {
        //given
        var getRequest = get("/api/wishes?sort=wrong,asc")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + memberToken);
        //when
        var result = mockMvc.perform(getRequest).andReturn();
        //then
        var response = getResponseMessage(result);
        Assertions.assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void deleteWishProducts(List<WishProductResponse> wishProductResponses) {
        for (var wishProductResponse : wishProductResponses) {
            wishProductService.deleteWishProduct(wishProductResponse.id());
        }
    }

    private ExceptionResponse getResponseMessage(MvcResult result) throws Exception {
        var resultString = result.getResponse().getContentAsString();
        return objectMapper.readValue(resultString, ExceptionResponse.class);
    }
}
