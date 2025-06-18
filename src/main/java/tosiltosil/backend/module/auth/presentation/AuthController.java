package tosiltosil.backend.module.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.auth.application.AuthService;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/local")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<Map<String, Object>> localSignUp(
            @RequestPart("memberInfo") @Valid final CreateLocalMemberRequest request,
            @RequestPart("profileImage") final MultipartFile profileImage
    ) {
        authService.localSignUp(request, profileImage);
        return Response.ok("정상적으로 일반 회원가입 되었습니다.");
    }
}
