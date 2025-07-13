package tosiltosil.backend.module.category.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
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
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.request.CategoryUpdateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryColorPerDayResponse;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
import tosiltosil.backend.module.category.domain.response.CurrentCategoryListResponse;
import tosiltosil.backend.module.goal.application.GoalService;
import tosiltosil.backend.module.stopwatch.application.StopwatchService;
import tosiltosil.backend.support.RestDocsTestSupport;

@SuppressWarnings("NonAsciiCharacters")
class CategoryControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private GoalService goalService;

    @MockitoBean
    private StopwatchService stopwatchService;

    @Test
    void 회원_카테고리_목록_조회() {
        // given
        List<CurrentCategoryListResponse> responses = List.of(
                new CurrentCategoryListResponse(1L, "운동", "#FF5733"),
                new CurrentCategoryListResponse(2L, "공부", "#33FF57")
        );

        given(categoryService.getCategoriesByMemberId(any(UUID.class)))
                .willReturn(responses);

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/categories")
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "카테고리 리스트 조회 성공",
                                "data": [
                                    {
                                        "categoryId": 1,
                                        "title": "운동",
                                        "color": "#FF5733"
                                    },
                                    {
                                        "categoryId": 2,
                                        "title": "공부",
                                        "color": "#33FF57"
                                    }
                                ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "카테고리 리스트 조회 성공"),
                                responseField("data", JsonFieldType.ARRAY, "카테고리 목록", "[]"),
                                responseField("data[].categoryId", JsonFieldType.NUMBER, "카테고리 ID", "1"),
                                responseField("data[].title", JsonFieldType.STRING, "카테고리 제목", "운동"),
                                responseField("data[].color", JsonFieldType.STRING, "카테고리 색상 (HEX 코드)", "#FF5733")
                        )
                ));
    }

    @Test
    void 회원_카테고리_목록_조회_빈_목록() {
        // given
        given(categoryService.getCategoriesByMemberId(any(UUID.class)))
                .willReturn(List.of());

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/categories")
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "카테고리 리스트 조회 성공",
                                "data": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 월별_카테고리_색상_조회() {
        // given
        int year = 2025;
        int month = 7;

        List<CategoryColorPerDayResponse> responses = List.of(
                new CategoryColorPerDayResponse(LocalDate.of(2025, 7, 8), List.of("#FF5733", "#33FF57")),
                new CategoryColorPerDayResponse(LocalDate.of(2025, 7, 15), List.of("#3357FF")),
                new CategoryColorPerDayResponse(LocalDate.of(2025, 7, 22), List.of("#FF33F5", "#FFAA33"))
        );

        given(categoryService.getCategoryColorPerMonth(any(UUID.class), eq(year), eq(month)))
                .willReturn(responses);

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/categories/color-per-day?year={year}&month={month}", year, month)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "월 별 카테고리 색상 조회 성공",
                                "data": [
                                    {
                                        "date": "2025-07-08",
                                        "color": ["#FF5733", "#33FF57"]
                                    },
                                    {
                                        "date": "2025-07-15",
                                        "color": ["#3357FF"]
                                    },
                                    {
                                        "date": "2025-07-22",
                                        "color": ["#FF33F5", "#FFAA33"]
                                    }
                                ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        queryParameters(
                                parameterWithName("year").description("조회할 년도 (1900-2100)"),
                                parameterWithName("month").description("조회할 월 (1-12)")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "월 별 카테고리 색상 조회 성공"),
                                responseField("data", JsonFieldType.ARRAY, "카테고리 색상 목록", "[]"),
                                responseField("data[].date", JsonFieldType.STRING, "날짜 (YYYY-MM-DD 형식)", "2025-07-08"),
                                responseField("data[].color", JsonFieldType.ARRAY, "해당 날짜의 카테고리 색상 리스트", "[\"#FF5733\", \"#33FF57\"]")
                        )
                ));
    }

    @Test
    void 월별_카테고리_색상_조회_빈_목록() {
        // given
        int year = 2025;
        int month = 8;

        given(categoryService.getCategoryColorPerMonth(any(UUID.class), eq(year), eq(month)))
                .willReturn(List.of());

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/categories/color-per-day?year={year}&month={month}", year, month)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "월 별 카테고리 색상 조회 성공",
                                "data": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 카테고리_생성하기() {
        // given
        String request = """
                    {
                        "title": "운동",
                        "color": "#FF5733"
                    }
                """;
        CategoryResponse response = CategoryResponse.of(1L);

        given(categoryService.createCategory(any(UUID.class), any(CategoryCreateRequest.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().isEqualTo("""
                            {
                                "status": 201,
                                "message": "카테고리가 정상적으로 생성되었습니다.",
                                "data": {
                                    "categoryId": 1
                                }
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        requestFields(
                                requestField("title", JsonFieldType.STRING, "카테고리 제목", true, "1글자 이상 10글자 이하", "운동"),
                                requestField("color", JsonFieldType.STRING, "카테고리 색상", true, "HEX 색상 코드", "#FF5733")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "201"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "카테고리가 정상적으로 생성되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}"),
                                responseField("data.categoryId", JsonFieldType.NUMBER, "생성된 카테고리 ID", "1")
                        )
                ));
    }

    @Test
    void 카테고리_생성_시_생성_제한_초과로_검증에_실패() {
        // given
        String request = """
                    {
                        "title": "운동",
                        "color": "#FF5733"
                    }
                """;
        given(categoryService.createCategory(any(UUID.class), any(CategoryCreateRequest.class)))
                .willThrow(new BadRequestException("생성 제한을 넘어 카테고리를 생성할 수 없습니다."));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "생성 제한을 넘어 카테고리를 생성할 수 없습니다.",
                                "errors": [ ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 카테고리_수정하기() {
        // given
        Long lastCategoryId = 1L;
        Long newCategoryId = 2L;
        String request = """
                    {
                        "title": "공부",
                        "color": "#33FF57"
                    }
                """;
        CategoryResponse response = CategoryResponse.of(newCategoryId);

        given(categoryService.updateCategory(any(UUID.class), any(Long.class), any(CategoryUpdateRequest.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.patch()
                .uri("/api/v1/categories/{categoryId}", lastCategoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "카테고리가 정상적으로 수정되었습니다.",
                                "data": {
                                    "categoryId": 2
                                }
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        pathParameters(
                                pathParameter("categoryId", "수정할 카테고리 ID")
                        ),
                        requestFields(
                                requestField("title", JsonFieldType.STRING, "카테고리 제목", true, "1글자 이상 10글자 이하", "공부"),
                                requestField("color", JsonFieldType.STRING, "카테고리 색상", true, "HEX 색상 코드", "#33FF57")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "카테고리가 정상적으로 수정되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}"),
                                responseField("data.categoryId", JsonFieldType.NUMBER, "수정된 새 카테고리 ID", "2")
                        )
                ));
    }

    @Test
    void 카테고리_삭제하기() {
        // given
        Long categoryId = 1L;
        CategoryResponse response = CategoryResponse.of(categoryId);

        given(categoryService.deleteCategory(any(UUID.class), any(Long.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.delete()
                .uri("/api/v1/categories/{categoryId}", categoryId)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .bodyJson().isEqualTo("""
                            {
                                "status": 200,
                                "message": "카테고리가 정상적으로 삭제되었습니다.",
                                "data": {
                                    "categoryId": 1
                                }
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document(
                        pathParameters(
                                pathParameter("categoryId", "삭제할 카테고리 ID")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "카테고리가 정상적으로 삭제되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}"),
                                responseField("data.categoryId", JsonFieldType.NUMBER, "삭제된 카테고리 ID", "1")
                        )
                ));
    }
}