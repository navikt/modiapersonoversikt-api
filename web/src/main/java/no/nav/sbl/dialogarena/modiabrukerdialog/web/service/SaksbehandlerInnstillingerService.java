package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.AnsattService;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.service.CookieHandler.getCookieUtils;


public class SaksbehandlerInnstillingerService {

    @Inject
    private AnsattService ansattService;

    public List<AnsattEnhet> hentEnhetsListe() {
        return ansattService.hentEnhetsliste();
    }

    public String getSaksbehandlerValgtEnhet() {
        if (getCookieUtils().load(brukerSpesifikCookieId()) == null) {
            return ansattService.hentEnhetsliste().get(0).enhetId;
        } else {
            return getCookieUtils().load(brukerSpesifikCookieId());
        }
    }

    public void setSaksbehandlerValgtEnhetCookie(String valgtEnhet) {
        getCookieUtils().save(brukerSpesifikCookieId(), valgtEnhet);
    }

    private String brukerSpesifikCookieId() {
        return "saksbehandlerinstillinger-" + getSubjectHandler().getUid();
    }
}

