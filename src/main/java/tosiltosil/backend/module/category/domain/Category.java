package tosiltosil.backend.module.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;
import tosiltosil.backend.common.domain.exception.ForbiddenException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private LocalDate date;

    @Builder
    private Category(
            final UUID memberId,
            final String title,
            final String color,
            final int sequence,
            final LocalDate date
    ) {
        this.memberId = memberId;
        this.title = title;
        this.color = color;
        this.sequence = sequence;
        this.date = date;
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
                .date(LocalDate.now())
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
}