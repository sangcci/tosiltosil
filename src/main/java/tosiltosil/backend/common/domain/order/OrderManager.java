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
     *
     * @param <T>       목록 내 엔티티의 타입으로, {@link Orderable} 인터페이스를 구현해야 합니다.
     * @param entities  순서 인덱스를 업데이트할 엔티티 목록입니다.
     * @return 순서 인덱스가 업데이트된 동일한 엔티티 목록입니다.
     */
    <T extends Orderable> List<T> renewOrderIndexes(List<T> entities);
}