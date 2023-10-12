package no.nav.modiapersonoversikt.service.unleash;

import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.beans.factory.annotation.Autowired;

import static java.util.stream.Collectors.joining;
import static no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.ENHETER_PROPERTY;

public class UnleashContextProviderImpl implements UnleashContextProvider {

    private final AnsattService ansattService;

    @Autowired
    public UnleashContextProviderImpl(AnsattService ansattService) {
        this.ansattService = ansattService;
    }

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
                .userId(ident)
                .remoteAddress(remoteAddr)
                .addProperty(ENHETER_PROPERTY, getEnheter())
                .build();
    }

    private String getEnheter() {
        return ansattService.hentEnhetsliste()
                .stream()
                .map(enhet -> enhet.enhetId)
                .collect(joining(","));
    }
}
