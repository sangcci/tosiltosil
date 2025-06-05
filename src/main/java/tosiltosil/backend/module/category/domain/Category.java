package tosiltosil.backend.module.category.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import tosiltosil.backend.common.domain.BaseEntity;

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

    @Builder
    private Category(
            final UUID memberId,
            final String title,
            final String color
    ) {
        this.memberId = memberId;
        this.title = title;
        this.color = color;
    }

    public static Category of(
            final UUID memberId,
            final String title,
            final String color
    ) {
        return Category.builder()
                .memberId(memberId)
                .title(title)
                .color(color)
                .build();
    }
}