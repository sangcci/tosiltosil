package tosiltosil.backend.module.category.domain.request;

public record CategoryOrderChangeRequest(
        Long categoryId,
        Integer targetIndex,
        Double prevOrderIndex,
        Double nextOrderIndex
) {

}
