package tosiltosil.backend.module.email.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.email.application.EmailService;
import tosiltosil.backend.module.email.domain.request.EmailSendRequest;
import tosiltosil.backend.module.email.domain.response.EmailSendResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public Response<EmailSendResponse> sendEmail(
            @CookieValue(name = "client-id", required = false) final UUID clientId,
            @Valid @RequestBody final EmailSendRequest request
    ) {
        EmailSendResponse response = emailService.sendEmail(clientId, request);
        return Response.ok("정상적으로 이메일을 전송했습니다.", response);
    }
}
