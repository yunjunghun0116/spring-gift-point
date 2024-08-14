package gift.controller;

import gift.controller.api.MemberApi;
import gift.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController implements MemberApi {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMember() {
        var memberId = getMemberId();
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    private Long getMemberId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal().toString();
        return Long.parseLong(principal);
    }
}
