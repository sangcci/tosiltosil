package tosiltosil.backend.module.category.application;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.domain.order.OrderManager;
import tosiltosil.backend.common.messaging.Events;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.CategoryRepository;
import tosiltosil.backend.module.category.domain.event.CategoryDeletedEvent;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.request.CategoryOrderChangeRequest;
import tosiltosil.backend.module.category.domain.request.CategoryUpdateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryColorPerDayResponse;
import tosiltosil.backend.module.category.domain.response.CategoryOrderChangeResponse;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
import tosiltosil.backend.module.category.domain.response.CurrentCategoryListResponse;
import tosiltosil.backend.module.category.domain.service.CategoryDomainService;
import tosiltosil.backend.module.category.domain.value.CategoryColor;
import tosiltosil.backend.module.goal.application.GoalService;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDomainService categoryDomainService;
    private final GoalService goalService;
    private final OrderManager orderManager;

    @Transactional(readOnly = true)
    public List<CurrentCategoryListResponse> getCategoriesByMemberId(
            final UUID memberId
    ) {
        List<Category> categories = categoryRepository.findCurrentCategories(memberId);
        return categories.stream().map(CurrentCategoryListResponse::of).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryColorPerDayResponse> getCategoryColorPerMonth(
            final UUID memberId,
            final int year,
            final int month
    ) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return categoryRepository.findColorsPerMonth(memberId, yearMonth);
    }

    @Transactional
    public CategoryResponse createCategory(
            final UUID memberId,
            final CategoryCreateRequest request
    ) {
        categoryDomainService.validateCategoryCreation(memberId);

        BigDecimal orderIndex = categoryRepository.findLastOrderIndex(memberId)
                .map(lastIndex -> orderManager.generateOrderIndexBetween(lastIndex, null))
                .orElse(orderManager.generateInitialOrderIndex());

        Category category = request.toEntity(memberId, orderIndex);
        Category savedCategory = categoryRepository.save(category);

        return CategoryResponse.of(savedCategory.getId());
    }

    @Transactional
    public CategoryResponse updateCategory(
            final UUID memberId,
            final Long categoryId,
            final CategoryUpdateRequest request
    ) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다."));
        category.validateIsMine(memberId);

        category.updateBasicInfo(request.title(), CategoryColor.valueOf(request.color()));

        return CategoryResponse.of(category.getId());
    }

    @Transactional
    public CategoryOrderChangeResponse changeOrder(
            final UUID memberId,
            final Long categoryId,
            final CategoryOrderChangeRequest request
    ) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다."));
        category.validateIsMine(memberId);

        if (!orderManager.validateIndexBounds(request.prevOrderIndex(), request.nextOrderIndex())) {
            renewOrderIndexes(memberId);
        }

        BigDecimal newOrderIndex = orderManager.generateOrderIndexBetween(request.prevOrderIndex(), request.nextOrderIndex());
        category.updateOrderIndex(newOrderIndex);

        categoryRepository.save(category);

        return CategoryOrderChangeResponse.of(newOrderIndex);
    }

    private void renewOrderIndexes(final UUID memberId) {
        List<Category> categories = categoryRepository.findCurrentCategories(memberId);

        List<Category> renewedCategories = orderManager.renewOrderIndexes(categories);

        categoryRepository.saveAll(renewedCategories);
    }

    @Transactional
    public CategoryResponse deleteCategory(
            final UUID memberId,
            final Long categoryId
    ) {
        // 카테고리 본인 로직인지 확인
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다."));
        category.validateIsMine(memberId);

        // 카테고리 내 목표의 총 성취 시간 계산
        Duration deletedTotalDuration = goalService.getGoalTotalDuration(memberId, categoryId);

        // 카테고리에 속한 목표 전부 삭제(커밋 이전) + 사용자 목표 성취 시간 차감(커밋 이후)
        Events.raise(
                CategoryDeletedEvent.of(memberId, categoryId, deletedTotalDuration)
        );

        // 카테고리 삭제
        categoryRepository.delete(category);

        return CategoryResponse.of(category.getId());
    }
}
