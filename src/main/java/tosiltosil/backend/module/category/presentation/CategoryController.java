package tosiltosil.backend.module.category.presentation;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CategoryResponse> createCategory(
            final UUID memberId,
            @RequestBody @Valid final CategoryCreateRequest request
    ) {
        CategoryResponse response = categoryService.createCategory(memberId, request);
        return Response.create("카테고리가 정상적으로 생성되었습니다.", response);
    }

    @Override
    @PatchMapping("/{categoryId}")
    public Response<CategoryResponse> updateCategory(
            final UUID memberId,
            @PathVariable final Long categoryId,
            @RequestBody @Valid final CategoryUpdateRequest request
    ) {
        CategoryResponse response = categoryService.updateCategory(memberId, categoryId, request);
        return Response.ok("카테고리가 정상적으로 수정되었습니다.", response);
    }

    @Override
    @PatchMapping("/{categoryId}/change-order")
    public void changeCategorySequence(
            final UUID memberId,
            @PathVariable final Long categoryId,
            @RequestBody @Valid final CategorySequenceChangeRequest request
    ) {
        categoryService.changeSequence(memberId, categoryId, request);
    }

    @Override
    @DeleteMapping("/{categoryId}")
    public Response<CategoryResponse> deleteCategory(
            final UUID memberId,
            @PathVariable final Long categoryId
    ) {
        CategoryResponse response = categoryService.deleteCategory(memberId, categoryId);
        return Response.ok("카테고리가 정상적으로 삭제되었습니다.", response);
    }
}
