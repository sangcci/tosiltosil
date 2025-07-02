package tosiltosil.backend.module.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.web.response.ErrorResponse;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.auth.domain.request.LocalLoginRequest;
import tosiltosil.backend.module.auth.domain.response.CreateLocalMemberResponse;
import tosiltosil.backend.module.auth.domain.response.LocalLoginResponse;

import java.util.Map;

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
                              "message": "필수 약관을 동의하지 않았습니다.",
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

    @Tag(name = "POST", description = "LOCAL LOGIN")
    @Operation(summary = "LOCAL 로그인", description = "이메일과 비밀번호로 LOCAL 로그인을 수행합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "정상적으로 로그인 되었습니다."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "입력값 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "입력값 오류",
                                    summary = "입력값이 유효하지 않을 때의 응답 예시",
                                    value = """
                                    {
                                      "status": 400,
                                      "message": "입력값 오류",
                                      "errors": [
                                        {
                                          "field": "email",
                                          "message": "이메일을 입력해주세요."
                                        },
                                        {
                                          "field": "password",
                                          "message": "비밀번호는 영문, 숫자, 특수문자를 포함하여 8글자 이상으로 입력해주세요."
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 이메일 또는 비밀번호 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 인증 정보",
                                    summary = "이메일 또는 비밀번호가 잘못되었을 때의 응답 예시",
                                    value = """
                                    {
                                      "status": 401,
                                      "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
                                      "errors": []
                                    }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<Response<LocalLoginResponse>> localLogin(
            @RequestBody @Valid LocalLoginRequest request
    );

    @Tag(name = "Auth", description = "인증 관련 API")
    @Operation(
            summary = "Access & Refresh Token 재발급",
            description = "Refresh Token을 받아 새로운 Access Token과 Refresh Token을 재발급합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "정상적으로 토큰을 재발급했습니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(
                                    name = "성공 응답 예시",
                                    value = """
                {
                  "status": 200,
                  "message": "정상적으로 토큰을 재발급했습니다.",
                  "data": {}
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 토큰",
                                    summary = "Refresh Token이 없거나 유효하지 않을 때",
                                    value = """
                {
                  "status": 401,
                  "message": "유효하지 않은 토큰입니다.",
                  "errors": []
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    summary = "토큰 만료 혹은 권한 없음",
                                    value = """
                {
                  "status": 401,
                  "message": "만료된 리프레시 토큰입니다.",
                  "errors": []
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    summary = "내부 서버 에러 발생",
                                    value = """
                {
                  "status": 500,
                  "message": "서버 오류가 발생했습니다.",
                  "errors": []
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<Response<Map<String, Object>>> reissueTokens(
            @Parameter(hidden = true)
            @CookieValue(name = "refresh-token") final String refreshToken
    );
}
