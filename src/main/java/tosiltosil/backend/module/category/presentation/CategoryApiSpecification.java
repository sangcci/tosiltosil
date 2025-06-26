package tosiltosil.backend.module.category.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tosiltosil.backend.common.web.response.ErrorResponse;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.request.CategorySequenceChangeRequest;
import tosiltosil.backend.module.category.domain.request.CategoryUpdateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;

import java.util.UUID;

public interface CategoryApiSpecification {

    @Tag(name = "Category", description = "카테고리 생성")
    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "카테고리가 정상적으로 생성되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "카테고리명 누락",
                                    summary = "카테고리명이 입력되지 않았을 때의 응답",
                                    value = """
                            {
                              "status": 400,
                              "message": "카테고리명을 입력해주세요.",
                              "errors": []
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "회원을 찾을 수 없음",
                                    summary = "회원 정보가 존재하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "회원을 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    Response<CategoryResponse> createGoal(UUID memberId, CategoryCreateRequest request);

    @Tag(name = "Category", description = "카테고리 수정")
    @Operation(summary = "카테고리 수정", description = "기존 카테고리의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "카테고리가 정상적으로 수정되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "카테고리 ID 오류",
                                    summary = "카테고리 ID가 유효하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 400,
                              "message": "카테고리 ID가 유효하지 않습니다. 카테고리 ID 숫자를 입력해야 합니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "카테고리 없음",
                                    summary = "해당 카테고리가 존재하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "카테고리를 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    Response<CategoryResponse> updateGoal(UUID memberId, Long categoryId, CategoryUpdateRequest request);

    @Tag(name = "Category", description = "카테고리 순서 변경")
    @Operation(summary = "카테고리 순서 변경", description = "카테고리의 순서를 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "카테고리 순서가 정상적으로 변경되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 순서값",
                                    summary = "순서값이 유효하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 400,
                              "message": "순서값이 유효하지 않습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "카테고리 없음",
                                    summary = "해당 카테고리가 존재하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "카테고리를 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    void changeCategorySequence(UUID memberId, Long categoryId, CategorySequenceChangeRequest request);

    @Tag(name = "Category", description = "카테고리 삭제")
    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "카테고리가 정상적으로 삭제되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "카테고리 ID 오류",
                                    summary = "카테고리 ID가 유효하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 400,
                              "message": "카테고리 ID가 유효하지 않습니다. 카테고리 ID 숫자를 입력해야 합니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "카테고리를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "카테고리 없음",
                                    summary = "해당 카테고리가 존재하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "카테고리를 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    Response<CategoryResponse> deleteGoal(UUID memberId, Long categoryId);
}