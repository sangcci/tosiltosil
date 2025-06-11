package tosiltosil.backend.module.category.domain.request;

import java.util.UUID;
import tosiltosil.backend.module.category.domain.Category;

public record CategoryCreateRequest(
        String title,
        String color
) {
    
    public Category toEntity(
            final UUID memberId
            //final int sequence
    ) {
        return Category.of(memberId, title, color);
    }
}