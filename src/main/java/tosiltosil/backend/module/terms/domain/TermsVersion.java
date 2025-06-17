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
public class TermsVersion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long termsId;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String content;

    @Builder
    private TermsVersion(
            final Long termsId,
            final String version,
            final String content
    ) {
        this.termsId = termsId;
        this.version = version;
        this.content = content;
    }

    public static TermsVersion of(
            final Long termsId,
            final String Version,
            final String content
    ) {
        return TermsVersion.builder()
                .termsId(termsId)
                .version(Version)
                .content(content)
                .build();
    }
}
