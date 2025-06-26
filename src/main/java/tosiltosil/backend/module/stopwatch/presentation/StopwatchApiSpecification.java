package tosiltosil.backend.module.stopwatch.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tosiltosil.backend.common.web.response.ErrorResponse;
import tosiltosil.backend.common.web.response.Response;

import java.util.UUID;

public interface StopwatchApiSpecification {

    @Tag(name = "Stopwatch", description = "스톱워치 시작")
    @Operation(summary = "스톱워치 시작", description = "특정 목표에 대한 스톱워치를 시작합니다. WebSocket을 통해 실시간으로 시작 이벤트가 전송됩니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스톱워치가 정상적으로 시작되었습니다."
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "스톱워치 상태 충돌",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이미 실행 중인 스톱워치",
                                    summary = "이미 스톱워치가 실행 중일 때의 응답",
                                    value = """
                            {
                              "status": 409,
                              "message": "이미 실행 중인 스톱워치가 있습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    Response<Void> startStopwatch(Long goalId, UUID memberId);

    @Tag(name = "Stopwatch", description = "스톱워치 정지")
    @Operation(summary = "스톱워치 정지", description = "특정 목표에 대한 스톱워치를 정지합니다. WebSocket을 통해 실시간으로 정지 이벤트가 전송됩니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스톱워치가 정상적으로 정지되었습니다."
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
                    description = "스톱워치를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "스톱워치 없음",
                                    summary = "실행 중인 스톱워치가 없을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "실행 중인 스톱워치를 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "스톱워치 상태 충돌",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이미 정지된 스톱워치",
                                    summary = "스톱워치가 이미 정지된 상태일 때의 응답",
                                    value = """
                            {
                              "status": 409,
                              "message": "스톱워치가 이미 정지된 상태입니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    Response<Void> pauseStopwatch(Long goalId, UUID memberId);
}