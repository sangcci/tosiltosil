package tosiltosil.backend.module.category.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.auth.annotation.LoginMember;
import tosiltosil.backend.common.web.response.Response;
import tosiltosil.backend.module.category.application.CategoryService;
import tosiltosil.backend.module.category.domain.request.CategoryCreateRequest;
import tosiltosil.backend.module.category.domain.request.CategoryOrderChangeRequest;
import tosiltosil.backend.module.category.domain.request.CategoryUpdateRequest;
import tosiltosil.backend.module.category.domain.response.CategoryColorPerDayResponse;
import tosiltosil.backend.module.category.domain.response.CategoryResponse;
import tosiltosil.backend.module.category.domain.response.CurrentCategoryListResponse;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Response<List<CurrentCategoryListResponse>> getCurrentCategories(
            @LoginMember final UUID memberId
    ) {
        List<CurrentCategoryListResponse> responses = categoryService.getCategoriesByMemberId(memberId);
        return Response.ok("카테고리 리스트 조회 성공", responses);
    }

    @GetMapping("/color-per-day")
    public Response<List<CategoryColorPerDayResponse>> getCategoryColorPerDay(
            @LoginMember final UUID memberId,
            @RequestParam final @Min(1900) @Max(2100) int year,
            @RequestParam final @Min(1) @Max(12) int month
    ) {
        List<CategoryColorPerDayResponse> responses = categoryService.getCategoryColorPerMonth(memberId, year, month);
        return Response.ok("월 별 카테고리 색상 조회 성공", responses);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CategoryResponse> createCategory(
            @LoginMember final UUID memberId,
            @RequestBody @Valid final CategoryCreateRequest request
    ) {
        CategoryResponse response = categoryService.createCategory(memberId, request);
        return Response.create("카테고리가 정상적으로 생성되었습니다.", response);
    }

    @PatchMapping("/{categoryId}")
    public Response<CategoryResponse> updateCategory(
            @LoginMember final UUID memberId,
            @PathVariable final Long categoryId,
            @RequestBody @Valid final CategoryUpdateRequest request
    ) {
        CategoryResponse response = categoryService.updateCategory(memberId, categoryId, request);
        return Response.ok("카테고리가 정상적으로 수정되었습니다.", response);
    }

    @PatchMapping("/{categoryId}/change-order")
    public Response<Map<String, Object>> changeCategoryOrder(
            @LoginMember final UUID memberId,
            @PathVariable final Long categoryId,
            @RequestBody @Valid final CategoryOrderChangeRequest request
    ) {
        categoryService.changeOrder(memberId, categoryId, request);
        return Response.ok("카테고리 순서가 정상적으로 변경되었습니다.");
    }

    @DeleteMapping("/{categoryId}")
    public Response<CategoryResponse> deleteCategory(
            @LoginMember final UUID memberId,
            @PathVariable final Long categoryId
    ) {
        CategoryResponse response = categoryService.deleteCategory(memberId, categoryId);
        return Response.ok("카테고리가 정상적으로 삭제되었습니다.", response);
    }
}
