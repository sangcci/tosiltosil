package tosiltosil.backend.module.category.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.common.domain.exception.ForbiddenException;
import tosiltosil.backend.common.domain.order.Orderable;
import tosiltosil.backend.module.category.domain.value.CategoryColor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity implements Orderable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryColor color;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal orderIndex;

    @Builder
    private Category(
            final UUID memberId,
            final String title,
            final CategoryColor color,
            final BigDecimal orderIndex
    ) {
        this.memberId = memberId;
        this.title = title;
        this.color = color;
        this.orderIndex = orderIndex;
    }

    public static Category of(
            final UUID memberId,
            final String title,
            final CategoryColor color,
            final BigDecimal orderIndex
    ) {
        return Category.builder()
                .memberId(memberId)
                .title(title)
                .color(color)
                .orderIndex(orderIndex)
                .build();
    }

    public void validateIsMine(final UUID memberId) {
        if (!Objects.equals(this.memberId, memberId)) {
            throw new ForbiddenException("해당 카테고리에 접근할 권한이 없습니다.");
        }
    }

    public void updateBasicInfo(final String title, final CategoryColor color) {
        this.title = title;
        this.color = color;
    }

    public void updateOrderIndex(final BigDecimal orderIndex) {
        this.orderIndex = orderIndex;
    }
}