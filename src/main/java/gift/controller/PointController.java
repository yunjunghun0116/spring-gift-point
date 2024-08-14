package gift.controller;

import gift.controller.api.PointApi;
import gift.dto.point.PointRequest;
import gift.dto.point.PointResponse;
import gift.service.PointService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
public class PointController implements PointApi {

    private final PointService pointService;

    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @PostMapping
    public ResponseEntity<PointResponse> addPoint(@Valid @RequestBody PointRequest pointRequest) {
        var memberId = getMemberId();
        var point = pointService.addPoint(memberId, pointRequest.point());
        return ResponseEntity.ok(point);
    }

    @GetMapping
    public ResponseEntity<PointResponse> getPoint() {
        var memberId = getMemberId();
        var point = pointService.getPoint(memberId);
        return ResponseEntity.ok(point);
    }

    private Long getMemberId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal().toString();
        return Long.parseLong(principal);
    }
}
