package tosiltosil.backend.common.domain.order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderManager {

    /**
     * 새로 생성된 엔티티의 초기 순서 인덱스를 생성합니다.
     *
     * @return 초기 순서 인덱스
     */
    BigDecimal generateInitialOrderIndex();

    /**
     * 두 개의 주어진 순서 인덱스 사이에 위치하는 새로운 순서 인덱스를 생성합니다. 이전 또는 다음 순서 인덱스가 null인 경우, 미리 정의된 증분을 사용하여 다른 인덱스를 기준으로 새 인덱스를 계산합니다.
     * 두 인덱스가 모두 제공된 경우, 두 값의 중간값을 계산합니다.
     *
     * @param prevOrderIndex 이전 순서 인덱스
     * @param nextOrderIndex 다음 순서 인덱스
     * @return 주어진 인덱스 사이의 순서 인덱스
     */
    BigDecimal generateOrderIndexBetween(BigDecimal prevOrderIndex, BigDecimal nextOrderIndex);

    /**
     * 순서 INDEX가 넘지 않았는지 확인하는 메서드입니다.
     *
     * 만일 순서를 넘었다면 {@link FractionalOrderManager} 순서 재정렬 메서드를 반드시 거쳐야 합니다.
     *
     * @param prevIndex 검증할 이전 인덱스
     * @param nextIndex 검증할 다음 인덱스
     * @return 인덱스가 범위 내에 있으면 true, 그렇지 않으면 false
     */
    boolean validateIndexBounds(BigDecimal prevIndex, BigDecimal nextIndex);

    /**
     * 순서를 재정렬 합니다.
     * - service 코드 내에서 사용할 때 먼저 validation을 거쳐야 합니다.
     * - 수정할 엔티티 수에 따라 쿼리 최적화를 진행하는 것이 좋습니다
     *
     * @param <T>       목록 내 엔티티의 타입으로, {@link Orderable} 인터페이스를 구현해야 합니다.
     * @param entities  순서 인덱스를 업데이트할 엔티티 목록입니다.
     * @return 순서 인덱스가 업데이트된 동일한 엔티티 목록입니다.
     */
    <T extends Orderable> List<T> renewOrderIndexes(List<T> entities);

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

    /**
     * 주어진 개수만큼 연속된 순서 인덱스 목록을 생성합니다.
     * 마지막 순서 인덱스 이후에 새로운 인덱스들을 순차적으로 생성합니다.
     *
     * @param lastOrderIndex 마지막 순서 인덱스 (null인 경우 초기 인덱스부터 시작)
     * @param count         생성할 인덱스 개수
     * @return 생성된 순서 인덱스 목록
     */
    List<BigDecimal> generateSequentialOrderIndexes(BigDecimal lastOrderIndex, int count);
}