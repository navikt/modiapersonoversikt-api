package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.FinnNAVKontorUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSEnhetsstatus;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSEnhetstyper;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OrganisasjonEnhetV2ServiceImplTest {

    @Mock
    private OrganisasjonEnhetV2 enhet;

    @InjectMocks
    private OrganisasjonEnhetV2ServiceImpl organisasjonEnhetServiceImpl;

    @BeforeEach
    void beforeEach() {
        initMocks(this);
    }

    @Test
    public void skalSortereEnheterIStigendeRekkefolge() {
        final WSHentFullstendigEnhetListeResponse response = new WSHentFullstendigEnhetListeResponse();
        response.getEnhetListe().addAll(asList(lagEnhet("3333"), lagEnhet("2222"), lagEnhet("1111")));
        when(enhet.hentFullstendigEnhetListe(any(WSHentFullstendigEnhetListeRequest.class))).thenReturn(response);

        final List<AnsattEnhet> enheter = organisasjonEnhetServiceImpl.hentAlleEnheter(OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE);

        assertThat(enheter.get(0).enhetId, is(equalTo("1111")));
        assertThat(enheter.get(1).enhetId, is(equalTo("2222")));
        assertThat(enheter.get(2).enhetId, is(equalTo("3333")));
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereHenteEnkeltEnhetGittEnhetId() throws Exception {
        final WSHentEnhetBolkResponse response = new WSHentEnhetBolkResponse();
        final WSOrganisasjonsenhet navEnhet = lagEnhet("0100");
        response.getEnhetListe().add(navEnhet);
        when(enhet.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(response);

        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100", OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE);

        assertTrue(enhetFraTjenesten.isPresent());
        assertThat(navEnhet.getEnhetId(), is(equalTo(enhetFraTjenesten.get().enhetId)));
        assertThat(navEnhet.getEnhetNavn(), is(equalTo(enhetFraTjenesten.get().enhetNavn)));
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereTomOptionalDersomEnhetIdReturnererTomRespons() throws Exception {
        WSHentEnhetBolkResponse hentEnhetBolkResponse = new WSHentEnhetBolkResponse();
        when(enhet.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(hentEnhetBolkResponse);
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100", OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE);
        assertFalse(enhetFraTjenesten.isPresent());
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereTomOptionalDersomEnhetIdInneholderUgyldigInput() throws Exception {
        when(enhet.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(new WSHentEnhetBolkResponse());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("x0100", OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.KUN_OPPGAVEBEHANDLERE);
        assertFalse(enhetFraTjenesten.isPresent());
    }

    @Test
    public void finnNAVKontorReturnererOptionalMedAnsattEnhetDersomWebserviceReturnererNAVKontor() throws Exception {
        final String enhetId = "1234";
        WSFinnNAVKontorResponse finnNAVKontorResponse = lagFinnNAVKontorResponse(enhetId);
        when(enhet.finnNAVKontor(any())).thenReturn(finnNAVKontorResponse);

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetServiceImpl.finnNAVKontor(enhetId, null);

        assertThat(ansattEnhet.isPresent(), is(true));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void finnNAVKontorReturnererOptionalMedKorrektMappetAnsattEnhetDersomWebserviceReturnererNAVKontor() throws Exception {
        when(enhet.finnNAVKontor(any())).thenReturn(lagFinnNAVKontorResponse("1234"));

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetServiceImpl.finnNAVKontor("1234", null);

        assertThat(ansattEnhet.isPresent(), is(true));
        assertThat(ansattEnhet.get().enhetId, is("1234"));
        assertThat(ansattEnhet.get().enhetNavn, is("MockEnhet"));
        assertThat(ansattEnhet.get().status, is("AKTIV"));
    }

    @Test
    public void finnNAVKontorReturnererTomOptionalDersomWebserviceReturnererNull() throws Exception {
        when(enhet.finnNAVKontor(any())).thenReturn(new WSFinnNAVKontorResponse());

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetServiceImpl.finnNAVKontor("1234", null);

        assertThat(ansattEnhet.isPresent(), is(false));
    }

    @Test
    public void finnNAVKontorReturnererTomOptionalDersomExceptionKastesFraWebservice() throws Exception {
        when(enhet.finnNAVKontor(any())).thenThrow(new FinnNAVKontorUgyldigInput());

        final Optional<AnsattEnhet> ansattEnhet = organisasjonEnhetServiceImpl.finnNAVKontor("1234", null);

        assertThat(ansattEnhet.isPresent(), is(false));
    }

    private WSOrganisasjonsenhet lagEnhet(final String enhetId) {
        WSOrganisasjonsenhet organisasjonsenhet = new WSOrganisasjonsenhet();
        organisasjonsenhet.setEnhetId(enhetId);
        organisasjonsenhet.setEnhetNavn("Enhet");
        organisasjonsenhet.setStatus(WSEnhetsstatus.AKTIV);
        return organisasjonsenhet;
    }

    private WSFinnNAVKontorResponse lagFinnNAVKontorResponse(String enhetId) {
        WSFinnNAVKontorResponse finnNAVKontorResponse = new WSFinnNAVKontorResponse();
        WSOrganisasjonsenhet organisasjonsenhet = new WSOrganisasjonsenhet();
        organisasjonsenhet.setEnhetId(enhetId);
        finnNAVKontorResponse.setNAVKontor(lagOrganisasjonsenhet(enhetId));
        return finnNAVKontorResponse;
    }

    private WSOrganisasjonsenhet lagOrganisasjonsenhet(String enhetId) {
        WSOrganisasjonsenhet organisasjonsenhet = new WSOrganisasjonsenhet();
        organisasjonsenhet.setStatus(WSEnhetsstatus.AKTIV);
        organisasjonsenhet.setEnhetNavn("MockEnhet");
        organisasjonsenhet.setEnhetId(enhetId);
        organisasjonsenhet.setOrganisasjonsnummer(enhetId);
        WSEnhetstyper enhetstyper = new WSEnhetstyper();
        enhetstyper.setValue("Mock");
        organisasjonsenhet.setType(enhetstyper);
        return organisasjonsenhet;
    }
}
