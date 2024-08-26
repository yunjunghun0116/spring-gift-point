package gift.service;

import gift.repository.MemberPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberPointService {

    private final MemberPointRepository memberPointRepository;
}
