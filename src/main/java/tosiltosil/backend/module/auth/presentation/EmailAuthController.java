package tosiltosil.backend.module.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.auth.application.EmailAuthService;
import tosiltosil.backend.module.auth.domain.request.email.EmailAuthRequest;
import tosiltosil.backend.module.auth.domain.request.email.EmailSendRequest;
import tosiltosil.backend.module.auth.domain.response.email.EmailAuthResponse;
import tosiltosil.backend.module.auth.domain.response.email.EmailSendResponse;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;
    private final CookieUtil cookieUtil;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public Response<EmailSendResponse> sendEmail(
            @Valid @RequestBody final EmailSendRequest request
    ) {
        EmailSendResponse response = emailAuthService.sendAuthEmail(request);
        return Response.ok("정상적으로 이메일을 전송했습니다.", response);
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Response<?>> verifyEmailAuth(
            @Valid @RequestBody final EmailAuthRequest request
    ) {
        EmailAuthResponse response = emailAuthService.verifyAuthEmail(request);
        String temporaryToken = response.temporaryToken();

        HttpHeaders headers = cookieUtil.generateTemporaryTokenCookies(temporaryToken);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(Response.ok("이메일 인증이 완료되었습니다."));
    }
}
