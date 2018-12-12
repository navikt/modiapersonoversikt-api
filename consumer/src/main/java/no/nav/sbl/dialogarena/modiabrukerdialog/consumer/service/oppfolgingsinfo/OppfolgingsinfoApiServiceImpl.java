package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.json.JsonUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.rest.OppfolgingsEnhetOgVeileder;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.rest.OppfolgingsStatus;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class OppfolgingsinfoApiServiceImpl implements OppfolgingsinfoApiService {
    private static final Logger logger = LoggerFactory.getLogger(OppfolgingsinfoApiServiceImpl.class);
    private String apiUrl;
    private HttpClient client;

    @Inject
    public OppfolgingsinfoApiServiceImpl(String apiUrl) {
        this.apiUrl = sluttMedSlash(apiUrl);
        this.client = lagHttpClient();
    }
    private String sluttMedSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    private HttpClient lagHttpClient() { return HttpClientBuilder.create().build(); }

    @Override
    public Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, LDAPService ldapService) {
        Oppfolgingsinfo info = null;
        try {
            OppfolgingsStatus status = hentOppfolgingStatus(fodselsnummer);
            OppfolgingsEnhetOgVeileder enhetOgVeileder = hentOppfolgingsEnhetOgVeileder(fodselsnummer);
            info = new Oppfolgingsinfo(status.isUnderOppfolging())
                    .withVeileder(hentSaksbehandler(enhetOgVeileder.getVeilederId(), ldapService))
                    .withOppfolgingsenhet(new AnsattEnhet(enhetOgVeileder.getOppfolgingsenhet().getEnhetId(), enhetOgVeileder.getOppfolgingsenhet().getNavn()));
        } catch (IOException ioe) {
            logger.error("Feil i oppfølgingsinfo: ", ioe);
        }
        return info;
    }

    public void ping() throws IOException {
        gjorSporring(hentPingURL());
    }

    private Saksbehandler hentSaksbehandler(String veilederIdent, LDAPService ldapService) {
        if (veilederIdent == null) {
            return null;
        }
        return ldapService.hentSaksbehandler(veilederIdent);
    }

    private OppfolgingsEnhetOgVeileder hentOppfolgingsEnhetOgVeileder(String fodselsnummer) throws IOException {
        return JsonUtils.fromJson(gjorSporring(hentOppfolgingsEnhetOgVeilederURL(fodselsnummer)), OppfolgingsEnhetOgVeileder.class);
    }

    private OppfolgingsStatus hentOppfolgingStatus(String fodselsnummer) throws IOException {
        return JsonUtils.fromJson(gjorSporring(hentOppfolgingsStatusURL(fodselsnummer)), OppfolgingsStatus.class);
    }

    private String hentOppfolgingsEnhetOgVeilederURL(String fodselsnummer) {
        return apiUrl + String.format("person/%s/oppfolgingsstatus", fodselsnummer);
    }

    private String hentOppfolgingsStatusURL(String fodselsnummer) {
        return apiUrl + String.format("oppfolging?fnr=%s", fodselsnummer);
    }

    private String hentPingURL() {
        return apiUrl + String.format("ping");
    }

    private InputStream gjorSporring(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        request.addHeader(AUTHORIZATION, "Bearer " + SubjectHandler.getSubjectHandler().getInternSsoToken());
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IllegalStateException("Oppfølging svarte med statuskode: " + statusCode);
        } else {
            return response.getEntity().getContent();
        }
    }
}
