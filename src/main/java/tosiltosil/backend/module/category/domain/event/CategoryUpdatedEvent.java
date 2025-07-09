package tosiltosil.backend.module.category.domain.event;

import java.util.UUID;

public record CategoryUpdatedEvent(
        UUID memberId,
        Long oldCategoryId,
        Long newCategoryId
) {
    
    public static CategoryUpdatedEvent of(final UUID memberId, final Long oldCategoryId, final Long newCategoryId) {
        return new CategoryUpdatedEvent(memberId, oldCategoryId, newCategoryId);
    }
}