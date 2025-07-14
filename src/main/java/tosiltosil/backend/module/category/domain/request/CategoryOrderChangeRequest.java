package tosiltosil.backend.module.category.domain.request;

public record CategoryOrderChangeRequest(
        Long categoryId,
        Integer targetIndex,
        String prevOrderKey,
        String nextOrderKey
) {

}
