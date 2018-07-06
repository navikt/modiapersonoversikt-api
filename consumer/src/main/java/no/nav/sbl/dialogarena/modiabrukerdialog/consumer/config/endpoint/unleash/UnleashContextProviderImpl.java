package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash;

import no.finn.unleash.UnleashContext;
import no.finn.unleash.UnleashContextProvider;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import java.util.HashMap;

public class UnleashContextProviderImpl implements UnleashContextProvider {

    private SubjectHandler subjectHandler;

    @Inject
    public UnleashContextProviderImpl(SubjectHandler subjectHandler) {
        this.subjectHandler = subjectHandler;
    }

    @Override
    public UnleashContext getContext() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String sessionId = attributes.getSessionId();
        String remoteAddress = attributes.getRequest().getRemoteAddr();

        String ident = subjectHandler.getUid();

        HashMap<String, String> properties = new HashMap<String, String>() {{
        }};

        return new UnleashContext(ident, sessionId, remoteAddress, properties);
    }
}
