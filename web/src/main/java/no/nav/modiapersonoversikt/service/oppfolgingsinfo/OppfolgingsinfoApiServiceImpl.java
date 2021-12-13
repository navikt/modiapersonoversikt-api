package no.nav.modiapersonoversikt.service.oppfolgingsinfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.common.auth.context.AuthContextHolderThreadLocal;
import no.nav.common.json.JsonMapper;
import no.nav.common.rest.client.RestClient;
import no.nav.modiapersonoversikt.infrastructure.http.AuthorizationInterceptor;
import no.nav.modiapersonoversikt.infrastructure.http.LoggingInterceptor;
import no.nav.modiapersonoversikt.infrastructure.http.XCorrelationIdInterceptor;
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet;
import no.nav.modiapersonoversikt.legacy.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.modiapersonoversikt.legacy.api.domain.oppfolgingsinfo.rest.OppfolgingsEnhetOgVeileder;
import no.nav.modiapersonoversikt.legacy.api.domain.oppfolgingsinfo.rest.OppfolgingsStatus;
import no.nav.modiapersonoversikt.legacy.api.domain.oppfolgingsinfo.rest.Oppfolgingsenhet;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.legacy.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class OppfolgingsinfoApiServiceImpl implements OppfolgingsinfoApiService {
    private ObjectMapper objectMapper = JsonMapper.defaultObjectMapper();
    private String apiUrl;
    private OkHttpClient client = RestClient
            .baseClient()
            .newBuilder()
            .addInterceptor(new XCorrelationIdInterceptor())
            .addInterceptor(new LoggingInterceptor("Oppfolging", LoggingInterceptor.DEFAULT_CONFIG, (request) -> request.header("X-Correlation-ID")))
            .addInterceptor(new AuthorizationInterceptor(() ->
                    AuthContextHolderThreadLocal.instance().requireIdTokenString()
            ))
            .build();

    @Autowired
    public OppfolgingsinfoApiServiceImpl(String apiUrl) {
        this.apiUrl = sluttMedSlash(apiUrl);
    }

    private String sluttMedSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    @Override
    public Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, LDAPService ldapService) {
        OppfolgingsStatus status = hentOppfolgingStatus(fodselsnummer);
        OppfolgingsEnhetOgVeileder enhetOgVeileder;
        if (status.isUnderOppfolging()) {
            enhetOgVeileder = hentOppfolgingsEnhetOgVeileder(fodselsnummer);
        } else {
            enhetOgVeileder = setTomEnhet();
        }
        return new Oppfolgingsinfo(status.isUnderOppfolging())
                .withVeileder(hentSaksbehandler(enhetOgVeileder.getVeilederId(), ldapService))
                .withOppfolgingsenhet(new AnsattEnhet(enhetOgVeileder.getOppfolgingsenhet().getEnhetId(), enhetOgVeileder.getOppfolgingsenhet().getNavn()));
    }

    //@NotNull
    private OppfolgingsEnhetOgVeileder setTomEnhet() {
        OppfolgingsEnhetOgVeileder enhetOgVeileder;
        enhetOgVeileder = new OppfolgingsEnhetOgVeileder();
        Oppfolgingsenhet oppfolgingsenhet = new Oppfolgingsenhet();
        oppfolgingsenhet.setEnhetId("");
        oppfolgingsenhet.setNavn("");
        enhetOgVeileder.setOppfolgingsenhet(oppfolgingsenhet);
        enhetOgVeileder.setVeilederId("");
        return enhetOgVeileder;
    }

    public void ping() throws IOException {
        RestClient
                .baseClient()
                .newCall(
                        new Request.Builder()
                                .url(hentPingURL())
                                .build()
                )
                .execute()
                .body()
                .string();
    }

    private Saksbehandler hentSaksbehandler(String veilederIdent, LDAPService ldapService) {
        if (veilederIdent == null) {
            return null;
        }
        return ldapService.hentSaksbehandler(veilederIdent);
    }

    private OppfolgingsEnhetOgVeileder hentOppfolgingsEnhetOgVeileder(String fodselsnummer) {
        return gjorSporring(hentOppfolgingsEnhetOgVeilederURL(fodselsnummer), OppfolgingsEnhetOgVeileder.class);
    }

    private OppfolgingsStatus hentOppfolgingStatus(String fodselsnummer) {
        return gjorSporring(hentOppfolgingsStatusURL(fodselsnummer), OppfolgingsStatus.class);
    }

    private String hentOppfolgingsEnhetOgVeilederURL(String fodselsnummer) {
        return apiUrl + String.format("person/%s/oppfolgingsstatus", fodselsnummer);
    }

    private String hentOppfolgingsStatusURL(String fodselsnummer) {
        return apiUrl + String.format("oppfolging?fnr=%s", fodselsnummer);
    }

    private String hentPingURL() {
        return apiUrl + "ping";
    }

    private <T> T gjorSporring(String url, Class<T> targetClass) {
        try {
            Request request = new Request
                    .Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            int statusCode = response.code();
            String body = response.body() != null ? response.body().string() : null;

            if (statusCode >= 200 && statusCode < 300 && body != null) {
                return objectMapper.readValue(body, targetClass);
            } else {
                throw new IllegalStateException("Forventet 200-range svar og body fra oppfolging-api, men fikk: " + statusCode + " " + body);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
