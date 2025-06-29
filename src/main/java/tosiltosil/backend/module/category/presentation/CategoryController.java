package tosiltosil.backend.module.category.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.category.application.CategoryService;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.request.CategorySequenceChangeRequest;
import tosiltosil.backend.module.category.domain.request.CategoryUpdateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryApiSpecification {

    private final CategoryService categoryService;

    @PostMapping
    @Override
    public Response<CategoryResponse> createGoal(
            final UUID memberId,
            @RequestBody @Valid final CategoryCreateRequest request
    ) {
        CategoryResponse response = categoryService.createCategory(memberId, request);
        return Response.create("카테고리가 정상적으로 생성되었습니다.", response);
    }

    @PatchMapping("/{categoryId}")
    @Override
    public Response<CategoryResponse> updateGoal(
            final UUID memberId,
            @PathVariable final Long categoryId,
            @RequestBody @Valid final CategoryUpdateRequest request
    ) {
        CategoryResponse response = categoryService.updateCategory(memberId, categoryId, request);
        return Response.ok("카테고리가 정상적으로 수정되었습니다.", response);
    }

    @PatchMapping("/{categoryId}/change-order")
    @Override
    public void changeCategorySequence(
            final UUID memberId,
            @PathVariable final Long categoryId,
            @RequestBody @Valid final CategorySequenceChangeRequest request
    ) {
        categoryService.changeSequence(memberId, categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    @Override
    public Response<CategoryResponse> deleteGoal(
            final UUID memberId,
            @PathVariable final Long categoryId
    ) {
        CategoryResponse response = categoryService.deleteCategory(memberId, categoryId);
        return Response.ok("카테고리가 정상적으로 삭제되었습니다.", response);
    }
}
