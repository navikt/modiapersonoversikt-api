package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonsenhet;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.OrganisasjonEnhetV2ServiceImpl;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSEnhetsstatus;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.informasjon.WSOrganisasjonsenhet;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.meldinger.*;
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
        final WSOrganisasjonsenhet navEnhet1 = new WSOrganisasjonsenhet();
        navEnhet1.setEnhetId("1111");
        navEnhet1.setEnhetNavn("Enhet");
        navEnhet1.setStatus(WSEnhetsstatus.AKTIV);
        final WSOrganisasjonsenhet navEnhet2 = new WSOrganisasjonsenhet();
        navEnhet2.setEnhetId("2222");
        navEnhet2.setEnhetNavn("Enhet");
        navEnhet2.setStatus(WSEnhetsstatus.AKTIV);
        final WSOrganisasjonsenhet navEnhet3 = new WSOrganisasjonsenhet();
        navEnhet3.setEnhetId("3333");
        navEnhet3.setEnhetNavn("Enhet");
        navEnhet3.setStatus(WSEnhetsstatus.AKTIV);
        response.getEnhetListe().addAll(asList(navEnhet3, navEnhet2, navEnhet1));
        when(enhetWS.hentFullstendigEnhetListe(any(WSHentFullstendigEnhetListeRequest.class))).thenReturn(response);

        final List<AnsattEnhet> enheter = organisasjonEnhetServiceImpl.hentAlleEnheter();

        assertThat(enheter.get(0).enhetId, is(equalTo("1111")));
        assertThat(enheter.get(1).enhetId, is(equalTo("2222")));
        assertThat(enheter.get(2).enhetId, is(equalTo("3333")));
    }


    @Test
    public void hentEnhetGittEnhetIdSkalReturnereHenteEnkeltEnhetGittEnhetId() throws Exception {
        final WSOrganisasjonsenhet navEnhet = new WSOrganisasjonsenhet();
        navEnhet.setEnhetId("0100");
        navEnhet.setEnhetNavn("Nav Østfold");
        navEnhet.setStatus(WSEnhetsstatus.AKTIV);
        final WSHentEnhetBolkResponse response = new WSHentEnhetBolkResponse();
        response.getEnhetListe().add(navEnhet);
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(response);

        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100");

        assertTrue(enhetFraTjenesten.isSome());
        assertThat(navEnhet.getEnhetId(), is(equalTo(enhetFraTjenesten.get().enhetId)));
        assertThat(navEnhet.getEnhetNavn(), is(equalTo(enhetFraTjenesten.get().enhetNavn)));
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereTomOptionalDersomEnhetIdReturnererTomRespons() throws Exception {
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(new WSHentEnhetBolkResponse());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("0100");
        assertFalse(enhetFraTjenesten.isSome());
    }

    @Test
    public void hentEnhetGittEnhetIdSkalReturnereTomOptionalDersomEnhetIdInneholderUgyldigInput() throws Exception {
        when(enhetWS.hentEnhetBolk(any(WSHentEnhetBolkRequest.class))).thenReturn(new WSHentEnhetBolkResponse());
        final Optional<AnsattEnhet> enhetFraTjenesten = organisasjonEnhetServiceImpl.hentEnhetGittEnhetId("x0100");
        assertFalse(enhetFraTjenesten.isSome());
    }

}