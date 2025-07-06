package tosiltosil.backend.module.stopwatch.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.module.category.application.CategoryService;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.stopwatch.application.StopwatchService;
import tosiltosil.backend.support.RestDocsTestSupport;

@SuppressWarnings("NonAsciiCharacters")
class StopwatchControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private StopwatchService stopwatchService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private GoalService goalService;

    @Test
    void 스톱워치_시작하기() {
        // given
        Long goalId = 1L;

        willDoNothing().given(stopwatchService).startStopwatch(any(UUID.class), any(Long.class));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/goals/{goalId}/stopwatch/start", goalId)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "스톱워치가 정상적으로 시작되었습니다.",
                                "data": null
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        pathParameters(
                                pathParameter("goalId", "스톱워치를 시작할 목표 ID")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "스톱워치가 정상적으로 시작되었습니다."),
                                responseField("data", JsonFieldType.NULL, "응답 데이터", "null")
                        )
                ));
    }

    @Test
    void 스톱워치_시작_시_이미_실행_중인_목표일_때_검증에_실패() {
        // given
        Long goalId = 1L;

        willThrow(new ConflictException("스톱워치가 이미 실행되거나 기간이 지난 상태입니다."))
                .given(stopwatchService).startStopwatch(any(UUID.class), any(Long.class));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/goals/{goalId}/stopwatch/start", goalId)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson().isEqualTo("""
                            {
                                "status": 409,
                                "message": "스톱워치가 이미 실행되거나 기간이 지난 상태입니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 스톱워치_정지하기() {
        // given
        Long goalId = 1L;

        willDoNothing().given(stopwatchService).pauseStopwatch(any(UUID.class), any(Long.class));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/goals/{goalId}/stopwatch/pause", goalId)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "스톱워치가 정상적으로 정지되었습니다.",
                                "data": null
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        pathParameters(
                                pathParameter("goalId", "스톱워치를 정지할 목표 ID")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "스톱워치가 정상적으로 정지되었습니다."),
                                responseField("data", JsonFieldType.NULL, "응답 데이터", "null")
                        )
                ));
    }

    @Test
    void 스톱워치_정지_시_이미_정지된_목표일_때_검증에_실패() throws Exception {
        // given
        Long goalId = 1L;

        willThrow(new ConflictException("스톱워치가 이미 정지되었습니다."))
                .given(stopwatchService).pauseStopwatch(any(UUID.class), any(Long.class));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/goals/{goalId}/stopwatch/pause", goalId)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson().isEqualTo("""
                            {
                                "status": 409,
                                "message": "스톱워치가 이미 정지되었습니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }
}