package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.PathlessCookieUtils;
import org.apache.wicket.util.cookies.CookieUtils;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet.TIL_ENHET_ID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.saksbehandlerInnstillingerCookieId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.saksbehandlerInnstillingerTimeoutCookieId;


public class SaksbehandlerInnstillingerServiceImpl implements SaksbehandlerInnstillingerService {

    private final AnsattService ansattService;

    @Inject
    public SaksbehandlerInnstillingerServiceImpl(AnsattService ansattService) {
        this.ansattService = ansattService;
    }


    public String getSaksbehandlerValgtEnhet() {
        List<String> ansattEnhetsIdListe = ansattService.hentEnhetsliste().stream()
                .map(TIL_ENHET_ID)
                .collect(toList());

        String enhetId = ansattEnhetsIdListe.get(0);

        if (valgtEnhetCookieEksisterer()) {
            return hentEnhetFraCookie(ansattEnhetsIdListe, enhetId);
        } else {
            setSaksbehandlerValgtEnhetCookie(enhetId);
            return enhetId;
        }
    }

    private String hentEnhetFraCookie(List<String> ansattEnhetsIdListe, String enhetId) {
        String cookieEnhetId = new PathlessCookieUtils().load(saksbehandlerInnstillingerCookieId());
        boolean saksbehanderHarTilgangTilEnhet = ansattEnhetsIdListe.contains(cookieEnhetId);
        return saksbehanderHarTilgangTilEnhet ? cookieEnhetId : enhetId;
    }

    public void setSaksbehandlerValgtEnhetCookie(String valgtEnhet) {
        CookieUtils cookieUtils = new PathlessCookieUtils();
        cookieUtils.getSettings().setMaxAge(3600 * 12);
        cookieUtils.save(saksbehandlerInnstillingerCookieId(), valgtEnhet);
        setSaksbehandlerInnstillingerTimeoutCookie();
    }

    public boolean saksbehandlerInnstillingerErUtdatert() {
        return new PathlessCookieUtils().load(saksbehandlerInnstillingerTimeoutCookieId()) == null;
    }

    public boolean valgtEnhetErKontaktsenter() {
        return getSaksbehandlerValgtEnhet().startsWith("41");
    }

    private boolean valgtEnhetCookieEksisterer() {
        return new PathlessCookieUtils().load(saksbehandlerInnstillingerCookieId()) != null;
    }

    private void setSaksbehandlerInnstillingerTimeoutCookie() {
        CookieUtils cookieUtils = new PathlessCookieUtils();
        cookieUtils.getSettings().setMaxAge(3600 * 12);
        cookieUtils.save(saksbehandlerInnstillingerTimeoutCookieId(), "");
    }
}

