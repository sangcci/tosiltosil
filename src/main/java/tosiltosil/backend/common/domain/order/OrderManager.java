package tosiltosil.backend.common.domain.order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderManager {

    /**
     * 주어진 마지막 순서 인덱스를 기반으로 다음 순차적 순서 인덱스를 생성합니다.
     * 마지막 순서 인덱스가 null이면 미리 정의된 초기값을 사용합니다.
     *
     * @param lastOrderIndex 마지막 순서 인덱스 (null인 경우 초기 순서 인덱스부터 시작)
     * @return 생성된 순서 인덱스
     */
    BigDecimal generateOrderIndex(BigDecimal lastOrderIndex);

    /**
     * 주어진 개수만큼 연속된 순서 인덱스 목록을 생성합니다.
     * 마지막 순서 인덱스 이후에 새로운 인덱스들을 순차적으로 생성합니다.
     *
     * @param lastOrderIndex 마지막 순서 인덱스 (null인 경우 초기 인덱스부터 시작)
     * @param count         생성할 인덱스 개수
     * @return 생성된 순서 인덱스 목록
     */
    List<BigDecimal> generateSequentialOrderIndexes(BigDecimal lastOrderIndex, int count);

    /**
     * 주어진 순서가 정렬된 엔티티 목록에서 지정된 위치로 순서를 변경하기 위한 새로운 순서 인덱스를 계산합니다.
     * 필요한 경우 엔티티들의 순서를 재정렬하고 새로운 순서 인덱스를 반환합니다.
     *
     * @param <T>           목록 내 엔티티의 타입으로, {@link Orderable} 인터페이스를 구현해야 합니다.
     * @param entities      순서가 정렬된 엔티티 목록입니다.
     * @param targetPosition 목표 위치 (1부터 시작)
     * @return 새로운 순서 인덱스
     */
    <T extends Orderable> BigDecimal calculateOrderIndexForPosition(List<T> entities, int targetPosition);
}