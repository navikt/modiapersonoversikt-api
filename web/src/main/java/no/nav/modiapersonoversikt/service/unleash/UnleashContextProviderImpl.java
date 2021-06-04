package no.nav.modiapersonoversikt.service.unleash;

import no.finn.unleash.UnleashContext;
import no.finn.unleash.UnleashContextProvider;
import no.nav.common.auth.subject.SubjectHandler;
import no.nav.modiapersonoversikt.api.service.norg.AnsattService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.beans.factory.annotation.Autowired;

import static java.util.stream.Collectors.joining;
import static no.nav.modiapersonoversikt.service.unleash.strategier.ByEnhetStrategy.ENHETER;

public class UnleashContextProviderImpl implements UnleashContextProvider {

    private AnsattService annsattService;

    @Autowired
    public UnleashContextProviderImpl(AnsattService annsattService) {
        this.annsattService = annsattService;
    }

    @Override
    public UnleashContext getContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String ident = SubjectHandler.getIdent().orElse(null);
        String remoteAddr = null;

        try {
            remoteAddr = attributes.getRequest().getRemoteAddr();
        } catch (Exception ignored) {

        }

        return UnleashContext.builder()
                .userId(ident)
                .remoteAddress(remoteAddr)
                .addProperty(ENHETER, getEnheter())
                .build();
    }

    private String getEnheter() {
        return annsattService.hentEnhetsliste()
                .stream()
                .map(enhet -> enhet.enhetId)
                .collect(joining(","));
    }
}
