package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSEnhetsstatus.AKTIV;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrganisasjonEnhetV2ServiceImplTest {

    @Mock
    private OrganisasjonEnhetV2 enhetWS;

    @InjectMocks
    private OrganisasjonEnhetV2ServiceImpl organisasjonEnhetServiceImpl;

    @Test
    public void skalSortereEnheterIStigendeRekkefolge() {
        final WSHentFullstendigEnhetListeResponse response = new WSHentFullstendigEnhetListeResponse();
        response.getEnhetListe().addAll(asList(lagEnhet("3333"), lagEnhet("2222"), lagEnhet("1111")));
        when(enhetWS.hentFullstendigEnhetListe(any(WSHentFullstendigEnhetListeRequest.class))).thenReturn(response);

        final List<AnsattEnhet> enheter = organisasjonEnhetServiceImpl.hentAlleEnheter();

        assertThat(enheter.get(0).enhetId, is(equalTo("1111")));
        assertThat(enheter.get(1).enhetId, is(equalTo("2222")));
        assertThat(enheter.get(2).enhetId, is(equalTo("3333")));
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereHenteEnkeltEnhetGittEnhetId() throws Exception {
        final WSHentEnhetBolkResponse response = new WSHentEnhetBolkResponse();
        final WSOrganisasjonsenhet navEnhet = lagEnhet("0100");
        response.getEnhetListe().add(navEnhet);
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(response);

        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100");

        assertTrue(enhetFraTjenesten.isPresent());
        assertThat(navEnhet.getEnhetId(), is(equalTo(enhetFraTjenesten.get().enhetId)));
        assertThat(navEnhet.getEnhetNavn(), is(equalTo(enhetFraTjenesten.get().enhetNavn)));
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereTomOptionalDersomEnhetIdReturnererTomRespons() throws Exception {
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(new WSHentEnhetBolkResponse());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100");
        assertFalse(enhetFraTjenesten.isPresent());
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereTomOptionalDersomEnhetIdInneholderUgyldigInput() throws Exception {
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(new WSHentEnhetBolkResponse());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("x0100");
        assertFalse(enhetFraTjenesten.isPresent());
    }

    @Test
    public void finnNAVKontorReturnererOptionalMedAnsattEnhetDersomWebserviceReturnererNAVKontor() throws Exception {
        when(enhetWS.finnNAVKontor(any())).thenReturn(new WSFinnNAVKontorResponse().withNAVKontor(lagEnhet("1234")));

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetServiceImpl.finnNAVKontor("1234", null);

        assertThat(ansattEnhet.isPresent(), is(true));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void finnNAVKontorReturnererOptionalMedKorrektMappetAnsattEnhetDersomWebserviceReturnererNAVKontor() throws Exception {
        when(enhetWS.finnNAVKontor(any())).thenReturn(new WSFinnNAVKontorResponse().withNAVKontor(lagEnhet("1234")));

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetServiceImpl.finnNAVKontor("1234", null);

        assertThat(ansattEnhet.isPresent(), is(true));
        assertThat(ansattEnhet.get().enhetId, is("1234"));
        assertThat(ansattEnhet.get().enhetNavn, is("Enhet"));
        assertThat(ansattEnhet.get().status, is("AKTIV"));
    }

    @Test
    public void finnNAVKontorReturnererTomOptionalDersomWebserviceReturnererNull() throws Exception {
        when(enhetWS.finnNAVKontor(any())).thenReturn(new WSFinnNAVKontorResponse().withNAVKontor(null));

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetServiceImpl.finnNAVKontor("1234", null);

        assertThat(ansattEnhet.isPresent(), is(false));
    }

    @Test
    public void finnNAVKontorReturnererTomOptionalDersomExcetpionKastesFraWebservice() throws Exception {
        when(enhetWS.finnNAVKontor(any())).thenThrow(new FinnNAVKontorUgyldigInput());

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetServiceImpl.finnNAVKontor("1234", null);

        assertThat(ansattEnhet.isPresent(), is(false));
    }

    private WSOrganisasjonsenhet lagEnhet(final String enhetId) {
        return new WSOrganisasjonsenhet().withEnhetId(enhetId).withEnhetNavn("Enhet").withStatus(AKTIV);
    }
}