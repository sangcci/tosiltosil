package tosiltosil.backend.module.member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.auth.annotation.LoginMember;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.module.member.domain.response.ProfileDto;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check-email")
    public Response<Map<String, Object>> validateEmailNotDuplicated(
            @RequestParam("email") final String email,
            @RequestParam("type") final String loginType
    ) {
        memberService.validateEmailNotDuplicated(email, loginType);
        return Response.ok("사용 가능한 이메일입니다.");
    }
}
