package tosiltosil.backend.module.email.domain;

import java.io.Serializable;

public record EmailAuthMeta (
        int sendCount,
        int authFailCount
) implements Serializable {

}
