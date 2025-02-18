package gift.service;

import gift.dto.giftorder.GiftOrderRequest;
import gift.dto.giftorder.GiftOrderResponse;
import gift.dto.option.OptionResponse;
import gift.dto.product.ProductBasicInformation;
import gift.exception.NotFoundElementException;
import gift.model.GiftOrder;
import gift.model.Option;
import gift.repository.GiftOrderRepository;
import gift.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GiftOrderService {

    private final GiftOrderRepository giftOrderRepository;
    private final MemberRepository memberRepository;
    private final WishProductService wishProductService;

    public GiftOrderResponse addGiftOrder(Long memberId, Option option, GiftOrderRequest giftOrderRequest) {
        var order = saveGiftOrderWithGiftOrderRequest(memberId, option, giftOrderRequest);
        wishProductService.deleteAllByMemberIdAndProductId(memberId, option.getProduct().getId());
        return getGiftOrderResponseFromGiftOrder(order);
    }

    @Transactional(readOnly = true)
    public GiftOrderResponse getGiftOrder(Long id) {
        var order = giftOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundElementException(id + "를 가진 주문이 존재하지 않습니다."));
        return getGiftOrderResponseFromGiftOrder(order);
    }

    @Transactional(readOnly = true)
    public List<GiftOrderResponse> getGiftOrders(Long memberId, Pageable pageable) {
        return giftOrderRepository.findAllByMemberId(memberId, pageable)
                .stream()
                .map(this::getGiftOrderResponseFromGiftOrder)
                .toList();
    }

    public void deleteOrder(Long id) {
        giftOrderRepository.deleteById(id);
    }

    public void deleteAllByOptionId(Long optionId) {
        giftOrderRepository.deleteAllByOptionId(optionId);
    }

    public void deleteAllByMemberId(Long memberId) {
        giftOrderRepository.deleteAllByMemberId(memberId);
    }

    private GiftOrder saveGiftOrderWithGiftOrderRequest(Long memberId, Option option, GiftOrderRequest giftOrderRequest) {
        var member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(memberId + "를 가진 이용자가 존재하지 않습니다."));
        var order = new GiftOrder(member, option, giftOrderRequest.quantity(), giftOrderRequest.message());
        return giftOrderRepository.save(order);
    }

    private GiftOrderResponse getGiftOrderResponseFromGiftOrder(GiftOrder giftOrder) {
        var product = giftOrder.getOption().getProduct();
        var productBasicInformation = ProductBasicInformation.of(product.getId(), product.getName(), product.getPrice());
        var optionResponse = OptionResponse.of(giftOrder.getId(), giftOrder.getOption().getProduct().getName(), giftOrder.getQuantity());
        return GiftOrderResponse.of(giftOrder.getId(), productBasicInformation, optionResponse, giftOrder.getQuantity(), giftOrder.getCreatedDate(), giftOrder.getMessage());
    }
}
