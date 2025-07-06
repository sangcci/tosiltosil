package tosiltosil.backend.module.category.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

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
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
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
        Long categoryId = 1L;
        String request = """
                    {
                        "title": "공부",
                        "color": "#33FF57"
                    }
                """;
        CategoryResponse response = CategoryResponse.of(categoryId);

        given(categoryService.updateCategory(any(UUID.class), any(Long.class), any(CategoryUpdateRequest.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.patch()
                .uri("/api/v1/categories/{categoryId}", categoryId)
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
                                    "categoryId": 1
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
                                responseField("data.categoryId", JsonFieldType.NUMBER, "카테고리 ID", "1")
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