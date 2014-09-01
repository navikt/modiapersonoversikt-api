package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.AnsattService;
import org.apache.wicket.util.cookies.CookieUtils;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet.ENHET_ID;


public class SaksbehandlerInnstillingerService {

    @Inject
    private AnsattService ansattService;

    public List<AnsattEnhet> hentEnhetsListe() {
        return ansattService.hentEnhetsliste();
    }

    public String getSaksbehandlerValgtEnhet() {
        List<String> ansattEnhetsIdListe = on(ansattService.hentEnhetsliste()).map(ENHET_ID).collect();
        String enhetId = ansattEnhetsIdListe.get(0);

        if (valgtEnhetCookieEksistererIkke()) {
            setSaksbehandlerValgtEnhetCookie(enhetId);
            return enhetId;
        } else {
            String cookieEnhetId = new CookieUtils().load(saksbehandlerInnstillingerCookieId());
            return ansattEnhetsIdListe.contains(cookieEnhetId) ? cookieEnhetId : enhetId;
        }
    }

    public void setSaksbehandlerValgtEnhetCookie(String valgtEnhet) {
        CookieUtils cookieUtils = new CookieUtils();
        cookieUtils.getSettings().setMaxAge(3600 * 24 * 365);
        cookieUtils.save(saksbehandlerInnstillingerCookieId(), valgtEnhet);

        setSaksbehandlerInnstillingerTimeoutCookie();
    }

    public boolean saksbehandlerInnstillingerErUtdatert() {
        return new CookieUtils().load(saksbehandlerInnstillingerTimeoutCookieId()) == null;
    }

    public boolean valgtEnhetErKontaktsenter() {
        return getSaksbehandlerValgtEnhet().startsWith("41");
    }

    private boolean valgtEnhetCookieEksistererIkke() {
        return new CookieUtils().load(saksbehandlerInnstillingerCookieId()) == null;
    }

    private void setSaksbehandlerInnstillingerTimeoutCookie() {
        CookieUtils cookieUtils = new CookieUtils();
        cookieUtils.getSettings().setMaxAge(3600 * 12);
        cookieUtils.save(saksbehandlerInnstillingerTimeoutCookieId(), "");
    }

    private String saksbehandlerInnstillingerTimeoutCookieId() {
        return "saksbehandlerinnstillinger-timeout-" + getSubjectHandler().getUid();
    }

    private String saksbehandlerInnstillingerCookieId() {
        return "saksbehandlerinnstillinger-" + getSubjectHandler().getUid();
    }
}

