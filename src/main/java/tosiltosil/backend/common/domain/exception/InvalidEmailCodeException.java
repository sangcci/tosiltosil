package tosiltosil.backend.common.domain.exception;

import lombok.Getter;

@Getter
public class InvalidEmailCodeException extends BadRequestException{

    private final int failCount;

    public InvalidEmailCodeException(int failCount) {
        super("금일 총 " + failCount + "회 틀렸습니다. 하루 최대 5회까지 가능합니다.");
        this.failCount = failCount;
    }
}
