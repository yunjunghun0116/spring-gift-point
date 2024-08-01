package gift.controller;

import gift.controller.api.WishProductApi;
import gift.dto.wishproduct.WishProductPageResponse;
import gift.dto.wishproduct.WishProductRequest;
import gift.dto.wishproduct.WishProductResponse;
import gift.service.WishProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/wishes")
public class WishProductController implements WishProductApi {

    private final WishProductService wishProductService;

    public WishProductController(WishProductService wishProductService) {
        this.wishProductService = wishProductService;
    }

    @PostMapping
    public ResponseEntity<WishProductResponse> addWishProduct(@Valid @RequestBody WishProductRequest wishProductRequest, @RequestAttribute("memberId") Long memberId) {
        var wishProduct = wishProductService.addWishProduct(wishProductRequest, memberId);
        return ResponseEntity.created(URI.create("/api/wishes/" + wishProduct.id())).body(wishProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishProductResponse> getWishProduct(@RequestAttribute("memberId") Long memberId, @PathVariable Long id) {
        var wishProduct = wishProductService.getWishProduct(memberId, id);
        return ResponseEntity.ok(wishProduct);
    }

    @GetMapping
    public ResponseEntity<WishProductPageResponse> getWishProducts(@RequestAttribute("memberId") Long memberId, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        var wishProducts = wishProductService.getWishProducts(memberId, pageable);
        return ResponseEntity.ok(wishProducts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishProduct(@PathVariable Long id) {
        wishProductService.deleteWishProduct(id);
        return ResponseEntity.noContent().build();
    }
}
