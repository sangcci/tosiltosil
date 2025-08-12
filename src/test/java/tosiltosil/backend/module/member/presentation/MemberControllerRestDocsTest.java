package tosiltosil.backend.module.member.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.module.member.application.MemberService;
import tosiltosil.backend.support.RestDocsTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

@WebMvcTest(MemberController.class)
@SuppressWarnings("NonAsciiCharacters")
public class MemberControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private MemberService memberService;

    @Test
    void 등록되지_않은_이메일로_이메일_중복_확인_시_성공() {
        // given
        String email = "test@example.com";
        String type = "LOCAL";

        willDoNothing().given(memberService).validateEmailIsExist(any(String.class), any(String.class));

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/members/email/exists?email={email}&type={type}", email, type)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "사용 가능한 이메일입니다.",
                                "data": {}
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        queryParameters(
                                queryParameter("email", "사용자 이메일"),
                                queryParameter("type", "로그인 타입 (LOCAL / SOCIAL)")
                                ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "사용 가능한 이메일입니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}")
                        )
                ));
    }

    @Test
    void 이미_등록된_이메일로_이메일_중복_확인_시_실패() {
        // given
        String email = "test@example.com";
        String type = "LOCAL";

        willThrow(new ConflictException("이미 등록된 이메일입니다."))
                .given(memberService).validateEmailIsExist(any(String.class), any(String.class));

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/members/email/exists?email={email}&type={type}", email, type)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson().isEqualTo("""
                        {
                            "status": 409,
                            "message": "이미 등록된 이메일입니다.",
                            "errors": []
                        }
                        """);

        assertThat(testResult).apply(documentHandler.document());
    }

}
