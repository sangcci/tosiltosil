package tosiltosil.backend.common.domain.exception;

import lombok.Getter;

@Getter
public class InvalidEmailCodeException extends BadRequestException{

    private final int failCount;

    public InvalidEmailCodeException(int failCount) {
        super("인증번호를 " + failCount + "회 틀렸습니다. 다시 확인해주세요.");
        this.failCount = failCount;
    }
}
