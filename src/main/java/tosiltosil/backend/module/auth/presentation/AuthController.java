package tosiltosil.backend.module.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.auth.application.AuthService;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.auth.domain.response.CreateLocalMemberResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiSpecification {

    private final AuthService authService;

    @PostMapping("/signup/local")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CreateLocalMemberResponse> localSignUp(
            @RequestPart("memberInfo") @Valid final CreateLocalMemberRequest request,
            @RequestPart(value = "profileImage", required = false) final MultipartFile profileImage
    ) {
        CreateLocalMemberResponse response = authService.localSignUp(request, profileImage);
        return Response.create("정상적으로 일반 회원가입 되었습니다.", response);
    }
}
