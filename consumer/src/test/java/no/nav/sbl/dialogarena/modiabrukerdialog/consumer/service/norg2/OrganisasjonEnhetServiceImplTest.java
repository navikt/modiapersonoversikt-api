package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.norg2;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.OrganisasjonEnhetV1;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.WSDetaljertEnhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.informasjon.WSEnheterForGeografiskNedslagsfelt;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSHentFullstendigEnhetListeRequest;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v1.meldinger.WSHentFullstendigEnhetListeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisasjonEnhetServiceImplTest {

    @Mock
    private OrganisasjonEnhetV1 enhetWS;

    @InjectMocks
    private OrganisasjonEnhetServiceImpl organisasjonEnhetServiceImpl;

    @Test
    public void skalSortereEnheterIStigendeRekkefolge() {
        final WSHentFullstendigEnhetListeResponse response = new WSHentFullstendigEnhetListeResponse();
        final WSDetaljertEnhet navEnhet1 = new WSDetaljertEnhet();
        navEnhet1.setEnhetId("1111");
        navEnhet1.setNavn("Enhet");
        final WSDetaljertEnhet navEnhet2 = new WSDetaljertEnhet();
        navEnhet2.setEnhetId("2222");
        navEnhet2.setNavn("Enhet");
        final WSDetaljertEnhet navEnhet3 = new WSDetaljertEnhet();
        navEnhet3.setEnhetId("3333");
        navEnhet3.setNavn("Enhet");
        response.getEnhetListe().addAll(asList(navEnhet3, navEnhet2, navEnhet1));
        when(enhetWS.hentFullstendigEnhetListe(any(WSHentFullstendigEnhetListeRequest.class))).thenReturn(response);

        final List<AnsattEnhet> enheter = organisasjonEnhetServiceImpl.hentAlleEnheter();

        assertThat(enheter.get(0).enhetId, is(equalTo("1111")));
        assertThat(enheter.get(1).enhetId, is(equalTo("2222")));
        assertThat(enheter.get(2).enhetId, is(equalTo("3333")));
    }

    @Test
    public void skalKunneHenteEnkeltEnhet() throws Exception {
        final WSDetaljertEnhet navEnhet = new WSDetaljertEnhet();
        navEnhet.setEnhetId("0219");
        navEnhet.setNavn("Nav Bærum");
        final WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse response = new WSFinnNAVKontorForGeografiskNedslagsfeltBolkResponse();
        final WSEnheterForGeografiskNedslagsfelt wsEnheterForGeografiskNedslagsfelt = new WSEnheterForGeografiskNedslagsfelt();
        wsEnheterForGeografiskNedslagsfelt.getEnhetListe().add(navEnhet);
        response.getEnheterForGeografiskNedslagsfeltListe().add(wsEnheterForGeografiskNedslagsfelt);
        when(enhetWS.finnNAVKontorForGeografiskNedslagsfeltBolk(any(WSFinnNAVKontorForGeografiskNedslagsfeltBolkRequest.class))).thenReturn(response);

        final AnsattEnhet enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhet("0219");

        assertThat(navEnhet.getEnhetId(), is(equalTo(enhetFraTjenesten.enhetId)));
        assertThat(navEnhet.getNavn(), is(equalTo(enhetFraTjenesten.enhetNavn)));
    }
}