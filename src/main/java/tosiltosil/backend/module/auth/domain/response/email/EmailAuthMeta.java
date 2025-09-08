package tosiltosil.backend.module.auth.domain.response.email;

import java.io.Serializable;

public record EmailAuthMeta (
        int sendCount,
        int authFailCount
) implements Serializable {

}
