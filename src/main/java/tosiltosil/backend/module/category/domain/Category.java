package tosiltosil.backend.module.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.common.domain.exception.ForbiddenException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE category SET deleted = true WHERE id = ?")
// @SQLRestriction("deleted == false")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int sequence;

    @Column(nullable = false)
    private boolean deleted = false;

    @Builder
    private Category(
            final UUID memberId,
            final String title,
            final String color,
            final int sequence
    ) {
        this.memberId = memberId;
        this.title = title;
        this.color = color;
        this.sequence = sequence;
    }

    public static Category of(
            final UUID memberId,
            final String title,
            final String color
            //final int sequence
    ) {
        return Category.builder()
                .memberId(memberId)
                .title(title)
                .color(color)
                .sequence(0)
                .build();
    }

    public void validateIsMine(final UUID memberId) {
        if (!Objects.equals(this.memberId, memberId)) {
            throw new ForbiddenException("해당 카테고리에 접근할 권한이 없습니다.");
        }
    }

    public void updateBasicInfo(
            final String title,
            final String color
    ) {
        this.title = title;
        this.color = color;
    }

    public Category createUpdatedCategory(
            final String title,
            final String color
    ) {
        return Category.builder()
                .memberId(this.memberId)
                .title(title)
                .color(color)
                .sequence(this.sequence)
                .build();
    }
}