package tosiltosil.backend.module.goal.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import java.math.BigDecimal;
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
import tosiltosil.backend.module.category.domain.value.CategoryColor;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.DayGoalsResponse;
import tosiltosil.backend.module.goal.domain.response.GoalListPerCategoryResponse;
import tosiltosil.backend.module.goal.domain.response.GoalListResponse;
import tosiltosil.backend.module.goal.domain.response.GoalIdResponse;
import tosiltosil.backend.module.goal.domain.response.GoalIdsResponse;
import tosiltosil.backend.module.goal.domain.value.GoalStatus;
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
        
        DayGoalsResponse response = new DayGoalsResponse(
                BigDecimal.valueOf(42),
                List.of(
                        new GoalListPerCategoryResponse(1L, "운동", CategoryColor.RED, List.of(
                                new GoalListResponse(1L, 1L, 1L, "운동하기", GoalStatus.BEFORE_STARTING, "PT2H", "PT0S"),
                                new GoalListResponse(2L, 1L, 2L, "독서하기", GoalStatus.RUNNING, "PT1H30M", "PT30M")
                        )),
                        new GoalListPerCategoryResponse(2L, "공부", CategoryColor.ORANGE, List.of(
                                new GoalListResponse(3L, 2L, 3L, "코딩하기", GoalStatus.RUNNING, "PT3H", "PT1H15M")
                        ))
                )
        );

        given(goalService.getDayGoals(any(UUID.class), any(UUID.class), any(LocalDate.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/goals/members/{memberId}?date={date}", memberId, date)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "목표 리스트 조회 성공",
                                "data": {
                                    "achievedPercentage": 42,
                                    "categories": [
                                        {
                                            "categoryId": 1,
                                            "categoryTitle": "운동",
                                            "categoryColor": "red",
                                            "goals": [
                                                {
                                                    "goalId": 1,
                                                    "categoryId": 1,
                                                    "iconId": 1,
                                                    "title": "운동하기",
                                                    "status": "시작 전",
                                                    "totalTime": "PT2H",
                                                    "duration": "PT0S"
                                                },
                                                {
                                                    "goalId": 2,
                                                    "categoryId": 1,
                                                    "iconId": 2,
                                                    "title": "독서하기",
                                                    "status": "진행 중",
                                                    "totalTime": "PT1H30M",
                                                    "duration": "PT30M"
                                                }
                                            ]
                                        },
                                        {
                                            "categoryId": 2,
                                            "categoryTitle": "공부",
                                            "categoryColor": "orange",
                                            "goals": [
                                                {
                                                    "goalId": 3,
                                                    "categoryId": 2,
                                                    "iconId": 3,
                                                    "title": "코딩하기",
                                                    "status": "진행 중",
                                                    "totalTime": "PT3H",
                                                    "duration": "PT1H15M"
                                                }
                                            ]
                                        }
                                    ]
                                }
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
                                responseField("data", JsonFieldType.OBJECT, "일별 목표 응답", "{}"),
                                responseField("data.achievedPercentage", JsonFieldType.NUMBER, "전체 목표 달성률 (%), 정수 표시", "41"),
                                responseField("data.categories", JsonFieldType.ARRAY, "카테고리별 목표 목록", "[]"),
                                responseField("data.categories[].categoryId", JsonFieldType.NUMBER, "카테고리 ID", "1"),
                                responseField("data.categories[].categoryTitle", JsonFieldType.STRING, "카테고리 제목", "운동"),
                                responseField("data.categories[].categoryColor", JsonFieldType.STRING, "카테고리 색상", "red"),
                                responseField("data.categories[].goals", JsonFieldType.ARRAY, "카테고리에 속한 목표 목록", "[]"),
                                responseField("data.categories[].goals[].goalId", JsonFieldType.NUMBER, "목표 ID", "1"),
                                responseField("data.categories[].goals[].categoryId", JsonFieldType.NUMBER, "카테고리 ID", "1"),
                                responseField("data.categories[].goals[].iconId", JsonFieldType.NUMBER, "아이콘 ID", "1"),
                                responseField("data.categories[].goals[].title", JsonFieldType.STRING, "목표 제목", "운동하기"),
                                responseField("data.categories[].goals[].status", JsonFieldType.STRING, "목표 상태 (시작 전, 진행 중, 완료, 실패)", "시작 전"),
                                responseField("data.categories[].goals[].totalTime", JsonFieldType.STRING, "목표 총 시간 (ISO-8601 Duration 형식)", "PT2H"),
                                responseField("data.categories[].goals[].duration", JsonFieldType.STRING, "현재까지 진행된 시간 (ISO-8601 Duration 형식)", "PT0S")
                        )
                ));
    }

    @Test
    void 회원_목표_목록_조회_빈_목록() {
        // given
        UUID memberId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        LocalDate date = LocalDate.of(2025, 7, 8);

        DayGoalsResponse emptyResponse = new DayGoalsResponse(BigDecimal.ZERO, List.of());

        given(goalService.getDayGoals(any(UUID.class), any(UUID.class), any(LocalDate.class)))
                .willReturn(emptyResponse);

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/goals/members/{memberId}?date={date}", memberId, date)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "목표 리스트 조회 성공",
                                "data": {
                                    "achievedPercentage": 0,
                                    "categories": []
                                }
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
    void 목표_순서_변경하기() {
        // given
        Long goalId = 1L;
        String request = """
                    {
                        "targetPosition": 2
                    }
                """;

        // when
        MvcTestResult testResult = mockMvcTester.patch()
                .uri("/api/v1/goals/{goalId}/change-order", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "목표 순서가 정상적으로 변경되었습니다.",
                                "data": {}
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        pathParameters(
                                pathParameter("goalId", "순서를 변경할 목표 ID")
                        ),
                        requestFields(
                                requestField("targetPosition", JsonFieldType.NUMBER, "목표 위치 (1부터 시작)", true, "1 이상의 정수", "2")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "목표 순서가 정상적으로 변경되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}")
                        )
                ));
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