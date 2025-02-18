package gift.service;

import gift.dto.giftorder.GiftOrderRequest;
import gift.dto.giftorder.GiftOrderResponse;
import gift.dto.option.OptionRequest;
import gift.dto.option.OptionResponse;
import gift.exception.BadRequestException;
import gift.exception.DuplicatedNameException;
import gift.exception.NotFoundElementException;
import gift.model.Option;
import gift.model.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;
    private final GiftOrderService giftOrderService;

    public OptionResponse addOption(Long productId, OptionRequest optionRequest) {
        optionNameValidation(productId, optionRequest.name());
        var option = saveOptionWithOptionRequest(productId, optionRequest);
        return getOptionResponseFromOption(option);
    }

    public void updateOption(Long productId, Long id, OptionRequest optionRequest) {
        var option = findOptionById(id);
        optionProductValidation(productId, option);
        option.updateOptionInfo(optionRequest.name(), optionRequest.quantity());
        optionRepository.save(option);
    }

    @Transactional(readOnly = true)
    public OptionResponse getOption(Long productId, Long id) {
        var option = findOptionById(id);
        optionProductValidation(productId, option);
        return getOptionResponseFromOption(option);
    }

    @Transactional(readOnly = true)
    public List<OptionResponse> getOptions(Long productId) {
        return optionRepository.findAllByProductId(productId)
                .stream()
                .map(this::getOptionResponseFromOption)
                .toList();
    }

    public void deleteOption(Long productId, Long optionId) {
        var option = findOptionById(optionId);
        optionProductValidation(productId, option);
        if (!optionRepository.existsById(optionId)) {
            throw new NotFoundElementException("존재하지 않는 상품 옵션의 ID 입니다.");
        }
        giftOrderService.deleteAllByOptionId(optionId);
        optionRepository.deleteById(optionId);
    }

    public void deleteAllByProductId(Long productId) {
        optionRepository.deleteAllByProductId(productId);
    }

    public GiftOrderResponse orderOption(Long memberId, GiftOrderRequest giftOrderRequest) {
        var option = subtractOptionQuantity(giftOrderRequest.optionId(), giftOrderRequest.quantity());
        return giftOrderService.addGiftOrder(memberId, option, giftOrderRequest);
    }

    private Option subtractOptionQuantity(Long id, Integer quantity) {
        var option = optionRepository.findByIdWithLock(id)
                .orElseThrow(() -> new NotFoundElementException(id + "를 가진 상품 옵션이 존재하지 않습니다."));
        option.subtract(quantity);
        return optionRepository.save(option);
    }

    private Option saveOptionWithOptionRequest(Long productId, OptionRequest optionRequest) {
        var product = findProductById(productId);
        var option = new Option(product, optionRequest.name(), optionRequest.quantity());
        return optionRepository.save(option);
    }

    private OptionResponse getOptionResponseFromOption(Option option) {
        return OptionResponse.of(option.getId(), option.getName(), option.getQuantity());
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundElementException(id + "를 가진 상품이 존재하지 않습니다."));
    }

    private Option findOptionById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new NotFoundElementException(id + "를 가진 상품 옵션이 존재하지 않습니다."));
    }

    private void optionNameValidation(Long productId, String name) {
        if (optionRepository.existsOptionByProductIdAndName(productId, name)) {
            throw new DuplicatedNameException("이미 존재하는 상품의 상품 옵션입니다.");
        }
    }

    private void optionProductValidation(Long productId, Option option) {
        if (!option.getProduct().getId().equals(productId)) {
            throw new BadRequestException("잘못된 접근입니다.");
        }
    }
}
