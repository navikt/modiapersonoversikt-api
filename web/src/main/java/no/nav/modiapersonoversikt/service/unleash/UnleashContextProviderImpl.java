package no.nav.modiapersonoversikt.service.unleash;

import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UnleashContextProviderImpl implements UnleashContextProvider {

    @Override
    public UnleashContext getContext() {
        String ident = AuthContextUtils.getIdent().orElse(null);
        String remoteAddr = null;

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            remoteAddr = attributes.getRequest().getRemoteAddr();
        } catch (Exception ignored) {

        }

        return UnleashContext.builder()
                .appName("modiapersonoversikt-api")
                .environment(System.getProperty("UNLEASH_ENVIRONMENT"))
                .userId(ident)
                .remoteAddress(remoteAddr)
                .build();
    }
}
