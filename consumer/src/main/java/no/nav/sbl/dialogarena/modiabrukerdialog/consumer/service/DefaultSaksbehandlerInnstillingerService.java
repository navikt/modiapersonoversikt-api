package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import org.apache.wicket.util.cookies.CookieUtils;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet.ENHET_ID;


public class DefaultSaksbehandlerInnstillingerService implements SaksbehandlerInnstillingerService {

    @Inject
    private AnsattService ansattService;

    public String getSaksbehandlerValgtEnhet() {
        List<String> ansattEnhetsIdListe = on(ansattService.hentEnhetsliste()).map(ENHET_ID).collect();
        String enhetId = ansattEnhetsIdListe.get(0);

        if (valgtEnhetCookieEksisterer()) {
            return hentEnhetFraCookie(ansattEnhetsIdListe, enhetId);
        } else {
            setSaksbehandlerValgtEnhetCookie(enhetId);
            return enhetId;
        }
    }

    private String hentEnhetFraCookie(List<String> ansattEnhetsIdListe, String enhetId) {
        String cookieEnhetId = new CookieUtils().load(saksbehandlerInnstillingerCookieId());
        boolean saksbehanderHarTilgangTilEnhet = ansattEnhetsIdListe.contains(cookieEnhetId);
        return saksbehanderHarTilgangTilEnhet ? cookieEnhetId : enhetId;
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

    private boolean valgtEnhetCookieEksisterer() {
        return new CookieUtils().load(saksbehandlerInnstillingerCookieId()) != null;
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

