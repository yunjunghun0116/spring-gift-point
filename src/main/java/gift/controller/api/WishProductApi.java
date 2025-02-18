package gift.controller.api;

import gift.dto.wishproduct.WishProductRequest;
import gift.dto.wishproduct.WishProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "위시리스트 API")
public interface WishProductApi {

    @Operation(summary = "회원의 위시 리스트에 상품을 추가한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "위시 리스트 추가 성공", content = @Content(schema = @Schema(implementation = WishProductResponse.class))),
            @ApiResponse(responseCode = "401", description = "허용되지 않는 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "내부 서버의 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<WishProductResponse> addWishProduct(WishProductRequest wishProductRequest);

    @Operation(summary = "회원의 특정 위시 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 위시 리스트 조회 성공", content = @Content(schema = @Schema(implementation = WishProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "특정 위시 리스트 조회 실패(사유 : 다른 사람의 위시 리스트는 접근할 수 없습니다.)", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "허용되지 않는 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "내부 서버의 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<WishProductResponse> getWishProduct(Long id);

    @Operation(summary = "회원의 위시 리스트에 있는 상품을 페이지 단위로 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 위시 리스트 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = WishProductResponse.class)))),
            @ApiResponse(responseCode = "401", description = "허용되지 않는 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "내부 서버의 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<List<WishProductResponse>> getWishProducts(Pageable pageable);

    @Operation(summary = "회원의 위시 리스트에서 상품을 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "위시 리스트 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "허용되지 않는 요청"),
            @ApiResponse(responseCode = "404", description = "위시 리스트 삭제 실패(사유 : 존재하지 않는 ID 입니다.)"),
            @ApiResponse(responseCode = "500", description = "내부 서버의 오류")
    })
    ResponseEntity<Void> deleteWishProduct(Long id);
}
