package tosiltosil.backend.module.goal.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import tosiltosil.backend.common.web.response.ErrorResponse;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.goal.domain.request.GoalCreateRequest;
import tosiltosil.backend.module.goal.domain.request.GoalSequenceChangeRequest;
import tosiltosil.backend.module.goal.domain.request.GoalUpdateRequest;
import tosiltosil.backend.module.goal.domain.response.GoalResponse;

public interface GoalApiSpecification {

    @Tag(name = "Goal", description = "목표 생성")
    @Operation(summary = "목표 생성", description = "새로운 목표를 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "목표가 정상적으로 생성되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "목표명 누락",
                                    summary = "목표명이 입력되지 않았을 때의 응답",
                                    value = """
                            {
                              "status": 400,
                              "message": "목표명을 입력해주세요.",
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
    Response<GoalResponse> createGoal(UUID memberId, GoalCreateRequest request);

    @Tag(name = "Goal", description = "목표 수정")
    @Operation(summary = "목표 수정", description = "기존 목표의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "목표가 정상적으로 수정되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "목표 ID 오류",
                                    summary = "목표 ID가 유효하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 400,
                              "message": "목표 ID가 유효하지 않습니다. 목표 ID 숫자를 입력해야 합니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "목표를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "목표 없음",
                                    summary = "해당 목표가 존재하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "목표를 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    Response<GoalResponse> updateGoal(UUID memberId, Long goalId, GoalUpdateRequest request);

    @Tag(name = "Goal", description = "목표 순서 변경")
    @Operation(summary = "목표 순서 변경", description = "목표의 순서를 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "목표 순서가 정상적으로 변경되었습니다."
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
                    description = "목표를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "목표 없음",
                                    summary = "해당 목표가 존재하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "목표를 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    void changeGoalSequence(UUID memberId, Long goalId, GoalSequenceChangeRequest request);

    @Tag(name = "Goal", description = "목표 삭제")
    @Operation(summary = "목표 삭제", description = "목표를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "목표가 정상적으로 삭제되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "목표 ID 오류",
                                    summary = "목표 ID가 유효하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 400,
                              "message": "목표 ID가 유효하지 않습니다. 목표 ID 숫자를 입력해야 합니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "목표를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "목표 없음",
                                    summary = "해당 목표가 존재하지 않을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "목표를 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    Response<GoalResponse> deleteGoal(UUID memberId, Long goalId);
}