package tosiltosil.backend.module.category.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tosiltosil.backend.module.category.domain.service.CategoryDomainService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class CategoryDomainServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryDomainService categoryDomainService;

    @Test
    void 카테고리_생성_시_10개_미만이면_성공한다() {
        // Given
        UUID memberId = UUID.randomUUID();
        when(categoryRepository.countByMemberId(memberId)).thenReturn(9L);

        // When & Then
        assertThatCode(() -> categoryDomainService.validateCategoryCreation(memberId))
                .doesNotThrowAnyException();
    }

    @Test
    void 카테고리_생성_시_10개_이상이면_실패한다() {
        // Given
        UUID memberId = UUID.randomUUID();
        when(categoryRepository.countByMemberId(memberId)).thenReturn(10L);

        // When & Then
        assertThatThrownBy(() -> categoryDomainService.validateCategoryCreation(memberId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("생성 제한을 넘어 카테고리를 생성할 수 없습니다.");
    }
}