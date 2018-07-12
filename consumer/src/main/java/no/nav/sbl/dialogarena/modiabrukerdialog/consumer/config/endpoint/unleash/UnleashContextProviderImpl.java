package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash;

import no.finn.unleash.UnleashContext;
import no.finn.unleash.UnleashContextProvider;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import java.util.HashMap;

import static java.util.stream.Collectors.joining;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.strategier.ByEnhetStrategy.ENHETER;

public class UnleashContextProviderImpl implements UnleashContextProvider {

    private SubjectHandler subjectHandler;
    private AnsattService annsattService;

    @Inject
    public UnleashContextProviderImpl(SubjectHandler subjectHandler, AnsattService annsattService) {
        this.subjectHandler = subjectHandler;
        this.annsattService = annsattService;
    }

    @Override
    public UnleashContext getContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String sessionId = attributes.getSessionId();
        String remoteAddress = attributes.getRequest().getRemoteAddr();

        String ident = subjectHandler.getUid();
        String ansattEnheter = getEnheter();

        HashMap<String, String> properties = new HashMap<String, String>() {{
            put(ENHETER, ansattEnheter);
        }};

        return new UnleashContext(ident, sessionId, remoteAddress, properties);
    }

    private String getEnheter() {
        return annsattService.hentEnhetsliste()
                    .stream()
                    .map(enhet -> enhet.enhetId)
                    .collect(joining(","));
    }
}
