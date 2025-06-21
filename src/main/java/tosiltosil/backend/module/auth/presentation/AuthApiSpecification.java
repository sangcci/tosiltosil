package tosiltosil.backend.module.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.web.response.ErrorResponse;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.auth.domain.response.CreateLocalMemberResponse;

public interface AuthApiSpecification {
    @Tag(name = "POST", description = "LOCAL SIGN UP")
    @Operation(summary = "LOCAL 회원가입", description = "LOCAL 회원을 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "정상적으로 회원가입 되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "필수 약관 미동의",
                                    summary = "필수 약관에 대해 동의하지 않았을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "필수 약관에 대해 동의하지 않았습니다.",
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
                                    name = "약관 동의를 찾을 수 없음",
                                    summary = "title과 version에 해당하는 약관이 없을 때의 응답",
                                    value = """
                            {
                              "status": 404,
                              "message": "약관을 찾을 수 없습니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "동일한 리소스 존재",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "중복 계정 오류",
                                    summary = "이미 존재하는 계정일 때의 응답",
                                    value = """
                            {
                              "status": 409,
                              "message": "이미 등록된 이메일입니다.",
                              "errors": []
                            }
                        """
                            )
                    )
            )
    })
    Response<CreateLocalMemberResponse> localSignUp(CreateLocalMemberRequest request, MultipartFile profileImage);
}
