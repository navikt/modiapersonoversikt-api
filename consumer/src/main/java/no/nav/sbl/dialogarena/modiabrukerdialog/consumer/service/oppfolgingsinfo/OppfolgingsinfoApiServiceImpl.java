package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.json.JsonUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.Oppfolgingsinfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.rest.Oppfolging;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.rest.Oppfolgingsstatus;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class OppfolgingsinfoApiServiceImpl implements OppfolgingsinfoApiService {
    private static final Logger logger = LoggerFactory.getLogger(OppfolgingsinfoApiServiceImpl.class);
    private String apiUrl;
    private HttpClient client;

    @Inject
    private LDAPService ldapService;

    public OppfolgingsinfoApiServiceImpl(String apiUrl) {
        this.apiUrl = apiUrl;
        this.client = lagHttpClient();

    }
    private HttpClient lagHttpClient() {
        return HttpClientBuilder.create().build();
    }

    @Override
    public Oppfolgingsinfo hentOppfolgingsinfo(String fodselsnummer, HttpServletRequest request) {
        Oppfolgingsinfo info = null;
        try {
            Oppfolgingsstatus status = hentOppfolgingsstatus(fodselsnummer);
            Oppfolging oppf = hentOppfolging(fodselsnummer);
            info = new Oppfolgingsinfo(oppf.isUnderOppfolging())
                    .withVeileder(hentSaksbehandler(status.getVeilederId()))
                    .withOppfolgingsenhet(new AnsattEnhet(status.getOppfolgingsenhet().getEnhetId(), status.getOppfolgingsenhet().getNavn()));
        } catch (IOException ioe) {
            logger.error("Feil i oppfølgingsinfo: ", ioe);
        }
        return info;
    }

    private Saksbehandler hentSaksbehandler(String veilederIdent) {
        return ldapService.hentSaksbehandler(veilederIdent);
    }

    private Oppfolgingsstatus hentOppfolgingsstatus(String fødselsnummer) throws IOException {
        return new ObjectMapper().readValue(gjorSporring(hentOppfølgingsstatusURL(fødselsnummer)), new TypeReference<Oppfolgingsstatus>() {
        });
    }

    private Oppfolging hentOppfolging(String fødselsnummer) throws IOException{
        return new ObjectMapper().readValue(gjorSporring(hentOppfølgingURL(fødselsnummer)), new TypeReference<Oppfolging>() {
        });
    }

    private String hentOppfølgingsstatusURL(String fødselsnummer){
        return apiUrl+String.format("person/%s/oppfolgingsstatus", fødselsnummer);
    }

    private String hentOppfølgingURL(String fødselsnummer){
        return apiUrl+String.format("oppfolging?fnr=%s", fødselsnummer);
    }

    private String gjorSporring(String url) throws IOException {
        logger.info("Prøver å hente fra " + url);
        HttpGet request = new HttpGet(url);
        request.addHeader(AUTHORIZATION, "Bearer " + SubjectHandler.getSubjectHandler().getInternSsoToken());
        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException(format("Kall på URL=\"%s\" returnerte respons med status=\"%s\"", url, response.getStatusLine()));
        }

        Oppfolgingsstatus map = JsonUtils.fromJson(response.getEntity().getContent(), Oppfolgingsstatus.class);

        String innhold = hentInnhold(response);
        logger.info("Innhold: " + innhold);
        return innhold;
    }

    private String hentInnhold(HttpResponse response) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
