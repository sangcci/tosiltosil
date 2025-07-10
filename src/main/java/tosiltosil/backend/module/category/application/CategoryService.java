package tosiltosil.backend.module.category.application;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.messaging.Events;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.CategoryRepository;
import tosiltosil.backend.module.category.domain.event.CategoryDeletedEvent;
import tosiltosil.backend.module.category.domain.event.CategoryUpdatedEvent;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.request.CategorySequenceChangeRequest;
import tosiltosil.backend.module.category.domain.request.CategoryUpdateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryColorPerDayResponse;
import tosiltosil.backend.module.category.domain.response.CurrentCategoryListResponse;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
import tosiltosil.backend.module.category.domain.service.CategoryDomainService;
import tosiltosil.backend.module.goal.application.CategoryGoalService;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDomainService categoryDomainService;
    private final CategoryGoalService categoryGoalService;

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

        // TODO: 순서 구현

        Category category = request.toEntity(memberId);
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

        Category newCategory = category.createUpdatedCategory(request.title(), request.color());
        Category savedNewCategory = categoryRepository.save(newCategory);

        Events.raise(CategoryUpdatedEvent.of(memberId, categoryId, savedNewCategory.getId()));

        categoryRepository.delete(category);

        return CategoryResponse.of(savedNewCategory.getId());
    }

    @Transactional
    public void changeSequence(
            final UUID memberId,
            final Long categoryId,
            final CategorySequenceChangeRequest request
    ) {
        // TODO: 순서 구현
    }

    @Transactional
    public CategoryResponse deleteCategory(
            final UUID memberId,
            final Long categoryId
    ) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("카테고리가 존재하지 않습니다."));
        category.validateIsMine(memberId);

        Duration deletedTotalDuration = categoryGoalService.deleteGoalsAndCalculateTotalDuration(memberId, categoryId);

        categoryRepository.delete(category);

        if (deletedTotalDuration.compareTo(Duration.ZERO) > 0) {
            Events.raise(CategoryDeletedEvent.of(memberId, deletedTotalDuration));
        }

        return CategoryResponse.of(category.getId());
    }
}
