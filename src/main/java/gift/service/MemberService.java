package gift.service;

import gift.exception.NotFoundElementException;
import gift.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final WishProductService wishProductService;
    private final KakaoService kakaoService;
    private final GiftOrderService giftOrderService;

    public void deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotFoundElementException("존재하지 않는 이용자의 ID 입니다.");
        }
        kakaoService.deleteByMemberId(memberId);
        giftOrderService.deleteAllByMemberId(memberId);
        wishProductService.deleteAllByMemberId(memberId);
        memberRepository.deleteById(memberId);
    }
}
