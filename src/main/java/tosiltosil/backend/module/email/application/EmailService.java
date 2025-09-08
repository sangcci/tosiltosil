package tosiltosil.backend.module.email.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String id;

    @Async("emailAsyncExecutor")
    public void sendEmail(String email, String subject, String content) {
        MimeMessagePreparator messagePreparator =
                mimeMessage -> {
                    final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

                    messageHelper.setFrom(id);
                    messageHelper.setTo(email);
                    messageHelper.setSubject(subject);
                    messageHelper.setText(content, true);
                };
        try {
            mailSender.send(messagePreparator);
        } catch(Exception e) {
            throw new RuntimeException("이메일 전송에 실패하였습니다.", e);
        }
    }
}
