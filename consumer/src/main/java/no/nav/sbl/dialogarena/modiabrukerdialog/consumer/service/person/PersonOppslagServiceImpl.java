package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.person;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.modig.common.MDCOperations;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.person.PersonOppslagService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.sts.StsServiceImpl;
import no.nav.sbl.rest.RestUtils;
import no.nav.tjenester.person.oppslag.v1.domain.Persondokument;

import javax.inject.Inject;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.RestConstants.*;

public class PersonOppslagServiceImpl implements PersonOppslagService {


    @Inject
    StsServiceImpl stsService;

    public Persondokument hentPersonDokument(String fnr) {
        String consumerOidcToken = stsService.hentConsumerOidcToken();
        String veilederOidcToken = SubjectHandler.getSubjectHandler().getInternSsoToken();

        return gjorSporring(PERSONDOKUMENTER_BASEURL, consumerOidcToken, veilederOidcToken, fnr);
    }

    private Persondokument gjorSporring(String url, String consumerOidcToken, String veilederOidcToken, String fnr) {
            return RestUtils.withClient(client -> client
                    .target(url)
                    .request()
                    .header(NAV_PERSONIDENT_HEADER, fnr)
                    .header(NAV_CALL_ID_HEADER, MDCOperations.generateCallId())
                    .header(AUTHORIZATION, AUTH_METHOD_BEARER + AUTH_SEPERATOR + veilederOidcToken)
                    .header(NAV_CONSUMER_TOKEN_HEADER, AUTH_METHOD_BEARER + AUTH_SEPERATOR + consumerOidcToken)
                    .header(TEMA_HEADER, ALLE_TEMA_HEADERVERDI)
                    .header(OPPLYSNINGSTYPER_HEADER, OPPLYSNINGSTYPER_HEADERVERDI)
                    .get(Persondokument.class)
            );

    }
}
