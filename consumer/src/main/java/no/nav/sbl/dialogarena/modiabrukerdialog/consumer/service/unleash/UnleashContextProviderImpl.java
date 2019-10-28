package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash;

import no.finn.unleash.UnleashContext;
import no.finn.unleash.UnleashContextProvider;
import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;

import static java.util.stream.Collectors.joining;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.strategier.ByEnhetStrategy.ENHETER;

public class UnleashContextProviderImpl implements UnleashContextProvider {

    private AnsattService annsattService;

    @Inject
    public UnleashContextProviderImpl(AnsattService annsattService) {
        this.annsattService = annsattService;
    }

    @Override
    public UnleashContext getContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
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
