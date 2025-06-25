package tosiltosil.backend.module.terms.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tosiltosil.backend.common.domain.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Terms extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean isRequired;

    @Builder
    private Terms(
            final String title,
            final boolean isRequired
    ) {
        this.title = title;
        this.isRequired = isRequired;
    }

    public static Terms of(
            final String title,
            final boolean isRequired
    ) {
        return Terms.builder()
                .title(title)
                .isRequired(isRequired)
                .build();
    }
}
