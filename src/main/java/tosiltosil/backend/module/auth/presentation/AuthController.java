package tosiltosil.backend.module.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.auth.application.AuthService;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.auth.domain.request.LocalLoginRequest;
import tosiltosil.backend.module.auth.domain.response.CreateLocalMemberResponse;
import tosiltosil.backend.module.auth.domain.response.LocalLoginResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiSpecification {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping(value = "/signup/local", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CreateLocalMemberResponse> localSignUp(
            @RequestPart("memberInfo") @Valid final CreateLocalMemberRequest request,
            @RequestPart(value = "profileImage", required = false) final MultipartFile profileImage
    ) {
        CreateLocalMemberResponse response = authService.localSignUp(request, profileImage);
        return Response.create("정상적으로 일반 회원가입 되었습니다.", response);
    }

    @PostMapping("/login/local")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Response<LocalLoginResponse>> localLogin(
            @Valid @RequestBody final LocalLoginRequest request
    ) {
        LocalLoginResponse response = authService.localLogin(request);
        HttpHeaders headers = cookieUtil.generateAccessAndRefreshTokenCookies(response.accessToken(), response.refreshToken());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(Response.ok("정상적으로 로그인 되었습니다.", response));
    }

    @GetMapping("/reissue")
    public ResponseEntity<Response<Map<String, Object>>> reissueAccessToken(
            @CookieValue(name = "refresh-token") final String refreshToken
    ) {
        String accessToken = authService.reissueAccessToken(refreshToken);
        HttpHeaders headers = cookieUtil.generateAccessTokenCookies(accessToken);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(Response.ok("정상적으로 엑세스 토큰을 재발급했습니다."));
    }
}
