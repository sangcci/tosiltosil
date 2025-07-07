package tosiltosil.backend.module.email.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.email.application.EmailService;
import tosiltosil.backend.module.email.domain.request.EmailAuthRequest;
import tosiltosil.backend.module.email.domain.request.EmailSendRequest;
import tosiltosil.backend.module.email.domain.response.EmailAuthResponse;
import tosiltosil.backend.module.email.domain.response.EmailSendResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final CookieUtil cookieUtil;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Response<EmailSendResponse>> sendEmail(
            @CookieValue(name = "client-id", required = false) final UUID clientId,
            @Valid @RequestBody final EmailSendRequest request
    ) {
        EmailSendResponse response = emailService.sendEmail(clientId, request);

        HttpHeaders headers = cookieUtil.generateClientIdCookie(response.clientId());
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(Response.ok("정상적으로 이메일을 전송했습니다.", response));
    }

    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Response<?>> verifyEmailAuth(
            @CookieValue(name = "client-id") final UUID clientId,
            @Valid @RequestBody final EmailAuthRequest request
    ) {
        EmailAuthResponse response = emailService.verifyEmailAuth(clientId, request);
        String temporaryToken = response.temporaryToken();

        HttpHeaders headers = cookieUtil.generateTemporaryTokenCookies(temporaryToken);
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(Response.ok("이메일 인증이 완료되었습니다."));
    }
}
