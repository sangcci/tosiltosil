package tosiltosil.backend.module.goal.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.module.category.application.CategoryService;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalIdResponse;
import tosiltosil.backend.module.goal.domain.response.GoalIdsResponse;
import tosiltosil.backend.module.goal.domain.response.GoalListResponse;
import tosiltosil.backend.module.stopwatch.application.StopwatchService;
import tosiltosil.backend.support.RestDocsTestSupport;

@SuppressWarnings("NonAsciiCharacters")
class GoalControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private GoalService goalService;

    @MockitoBean
    private StopwatchService stopwatchService;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void 회원_목표_목록_조회() {
        // given
        UUID memberId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDate date = LocalDate.of(2025, 7, 8);
        
        List<GoalListResponse> responses = List.of(
                new GoalListResponse(1L, 1L, 1L, "운동하기", "BEFORE_STARTING", "PT2H", "PT0S"),
                new GoalListResponse(2L, 1L, 2L, "독서하기", "RUNNING", "PT1H30M", "PT30M"),
                new GoalListResponse(3L, 2L, 3L, "코딩하기", "PAUSED", "PT3H", "PT1H15M")
        );

        given(goalService.getGoalsByMemberId(any(UUID.class), any(UUID.class), any(LocalDate.class)))
                .willReturn(responses);

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/goals/members/{memberId}/goals?date={date}", memberId, date)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "목표 리스트 조회 성공",
                                "data": [
                                    {
                                        "goalId": 1,
                                        "categoryId": 1,
                                        "iconId": 1,
                                        "title": "운동하기",
                                        "status": "BEFORE_STARTING",
                                        "totalTime": "PT2H",
                                        "duration": "PT0S"
                                    },
                                    {
                                        "goalId": 2,
                                        "categoryId": 1,
                                        "iconId": 2,
                                        "title": "독서하기",
                                        "status": "RUNNING",
                                        "totalTime": "PT1H30M",
                                        "duration": "PT30M"
                                    },
                                    {
                                        "goalId": 3,
                                        "categoryId": 2,
                                        "iconId": 3,
                                        "title": "코딩하기",
                                        "status": "PAUSED",
                                        "totalTime": "PT3H",
                                        "duration": "PT1H15M"
                                    }
                                ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        pathParameters(
                                pathParameter("memberId", "조회할 회원 ID")
                        ),
                        queryParameters(
                                queryParameter("date", "조회할 날짜 (YYYY-MM-DD 형식)")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메세지", "목표 리스트 조회 성공"),
                                responseField("data", JsonFieldType.ARRAY, "목표 목록", "[]"),
                                responseField("data[].goalId", JsonFieldType.NUMBER, "목표 ID", "1"),
                                responseField("data[].categoryId", JsonFieldType.NUMBER, "카테고리 ID", "1"),
                                responseField("data[].iconId", JsonFieldType.NUMBER, "아이콘 ID", "1"),
                                responseField("data[].title", JsonFieldType.STRING, "목표 제목", "운동하기"),
                                responseField("data[].status", JsonFieldType.STRING, "목표 상태 (BEFORE_STARTING, RUNNING, PAUSED, COMPLETED, FAILED)", "BEFORE_STARTING"),
                                responseField("data[].totalTime", JsonFieldType.STRING, "목표 총 시간 (ISO-8601 Duration 형식)", "PT2H"),
                                responseField("data[].duration", JsonFieldType.STRING, "현재까지 진행된 시간 (ISO-8601 Duration 형식)", "PT0S")
                        )
                ));
    }

    @Test
    void 회원_목표_목록_조회_빈_목록() {
        // given
        UUID memberId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDate date = LocalDate.of(2025, 7, 8);

        given(goalService.getGoalsByMemberId(any(UUID.class), any(UUID.class), any(LocalDate.class)))
                .willReturn(List.of());

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/goals/members/{memberId}/goals?date={date}", memberId, date)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "목표 리스트 조회 성공",
                                "data": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 목표_생성하기() {
        // given
        String request = """
                    {
                        "title": "운동하기",
                        "iconId": 1,
                        "categoryId": 1,
                        "dates": ["2025-07-06", "2025-07-07"],
                        "time": "PT2H30M"
                    }
                """;
        GoalIdsResponse response = GoalIdsResponse.of(List.of(1L, 2L));

        given(goalService.createGoal(any(UUID.class), any(GoalCreateRequest.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().isEqualTo("""
                            {
                                "status": 201,
                                "message": "목표가 정상적으로 생성되었습니다.",
                                "data": {
                                    "goalIds": [1, 2]
                                }
                            }
                        """);

        assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .apply(documentHandler.document(
                        requestFields(
                                requestField("title", JsonFieldType.STRING, "목표 제목", true, "1글자 이상, 20글자 이하. 한글, 영어, 숫자, 이모지, 공백 가능", "목표"),
                                requestField("iconId", JsonFieldType.NUMBER, "아이콘 ID", false, "1-12 범위", "5"),
                                requestField("categoryId", JsonFieldType.NUMBER, "카테고리 ID", true, "default = 카테고리 X", "1"),
                                requestField("dates[]", JsonFieldType.ARRAY, "목표 적용 날짜 목록", false, "최소 1개 이상, YYYY-MM-DD 형식, 오늘 이후 날짜만 가능", "['2025-07-06', '2024-07-07']"),
                                requestField("time", JsonFieldType.STRING, "목표 시간", false, "PT{hour}H{minute}M{second}S 형식", "PT2H30M")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "201"),
                                responseField("message", JsonFieldType.STRING, "응답 메세지", "목표가 정상적으로 생성되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}"),
                                responseField("data.goalIds", JsonFieldType.ARRAY, "생성된 목표 ID 목록", "[1, 2]")
                        )
                ));
    }

    @Test
    void 목표_생성_시_시간_검증에_실패() {
        // given
        String request = """
                    {
                        "title": "운동하기",
                        "iconId": 1,
                        "categoryId": 1,
                        "dates": ["2025-07-06", "2025-07-07"],
                        "time": "PT0H"
                    }
                """;
        given(goalService.createGoal(any(UUID.class), any(GoalCreateRequest.class)))
                .willThrow(new BadRequestException("시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다."));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다.",
                                "errors": [ ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 목표_생성_시_날짜_검증에_실패() {
        // given
        String request = """
                    {
                        "title": "운동하기",
                        "iconId": 1,
                        "categoryId": 1,
                        "dates": ["2025-07-01", "2025-07-02"],
                        "time": "PT1H30M"
                    }
                """;
        given(goalService.createGoal(any(UUID.class), any(GoalCreateRequest.class)))
                .willThrow(new BadRequestException("날짜는 오늘 이후여야 합니다."));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "날짜는 오늘 이후여야 합니다.",
                                "errors": [ ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 목표_수정하기() {
        // given
        Long goalId = 1L;
        String request = """
                    {
                        "title": "공부하기",
                        "iconId": 1,
                        "categoryId": 1,
                        "date": "2025-07-06",
                        "time": "PT01H30M"
                    }
                """;
        GoalIdResponse response = GoalIdResponse.of(goalId);

        given(goalService.updateGoal(any(UUID.class), any(Long.class), any(GoalUpdateRequest.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.patch()
                .uri("/api/v1/goals/{goalId}", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "목표가 정상적으로 수정되었습니다.",
                                "data": {
                                    "goalId": 1
                                }
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        pathParameters(
                                pathParameter("goalId", "수정할 목표 Id")
                        ),
                        requestFields(
                                requestField("title", JsonFieldType.STRING, "목표 제목", true, "1글자 이상, 20글자 이하. 한글, 영어, 숫자, 이모지, 공백 가능", "목표"),
                                requestField("iconId", JsonFieldType.NUMBER, "아이콘 ID", false, "1-12 범위", "5"),
                                requestField("categoryId", JsonFieldType.NUMBER, "카테고리 ID", true, "default = 카테고리 X", "1"),
                                requestField("date", JsonFieldType.STRING, "목표 적용 날짜", false, "YYYY-MM-DD 형식, 오늘 이후 날짜만 가능", "2025-07-06"),
                                requestField("time", JsonFieldType.STRING, "목표 총 시간", false, "PT{hour}H{minute}M{second}S 형식", "PT1H30M")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메세지", "목표가 정상적으로 수정되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}"),
                                responseField("data.goalId", JsonFieldType.NUMBER, "목표 ID", "1")
                        )
                ));
    }

    @Test
    void 목표_수정_시_날짜_검증에_실패() {
        // given
        Long goalId = 1L;
        String request = """
                    {
                        "title": "공부하기",
                        "iconId": 1,
                        "categoryId": 1,
                        "date": "2025-07-01",
                        "time": "PT01H30M"
                    }
                """;
        
        given(goalService.updateGoal(any(UUID.class), any(Long.class), any(GoalUpdateRequest.class)))
                .willThrow(new BadRequestException("날짜는 오늘 이후여야 합니다."));

        // when
        MvcTestResult testResult = mockMvcTester.patch()
                .uri("/api/v1/goals/{goalId}", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "날짜는 오늘 이후여야 합니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 목표_수정_시_시간_검증에_실패() {
        // given
        Long goalId = 1L;
        String request = """
                    {
                        "title": "공부하기",
                        "iconId": 1,
                        "categoryId": 1,
                        "date": "2025-07-10",
                        "time": "PT0H"
                    }
                """;
        
        given(goalService.updateGoal(any(UUID.class), any(Long.class), any(GoalUpdateRequest.class)))
                .willThrow(new BadRequestException("시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다"));

        // when
        MvcTestResult testResult = mockMvcTester.patch()
                .uri("/api/v1/goals/{goalId}", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 목표_삭제하기() {
        // given
        Long goalId = 1L;
        String request = """
                    {
                        "goalId": 1
                    }
                """;
        GoalIdResponse response = GoalIdResponse.of(goalId);

        given(goalService.deleteGoal(any(UUID.class), any(Long.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.delete()
                .uri("/api/v1/goals/{goalId}", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "목표가 정상적으로 삭제되었습니다.",
                                "data": {
                                    "goalId": 1
                                }
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        pathParameters(
                                pathParameter("goalId", "삭제할 목표 Id")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메세지", "목표가 정상적으로 삭제되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}"),
                                responseField("data.goalId", JsonFieldType.NUMBER, "삭제된 목표 ID", "1")
                        )
                ));
    }
}