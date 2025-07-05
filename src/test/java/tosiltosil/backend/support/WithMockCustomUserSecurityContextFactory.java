package tosiltosil.backend.support;

import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import tosiltosil.backend.common.auth.AuthDetails;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        AuthDetails principal = new AuthDetails(UUID.fromString(customUser.memberId()));
        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(principal, null, null);
        context.setAuthentication(auth);
        return context;
    }
}