package no.nav.kjerneinfo.consumer.fim.person.vergemal;

import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge;
import no.nav.kjerneinfo.domain.person.Fodselsnummer;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.Personnavn;
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.HentVergePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.HentVergeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentVergeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static no.nav.kjerneinfo.consumer.fim.person.vergemal.VergemalService.TPS_VERGES_FNR_MANGLENDE_DATA;
import static no.nav.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VergemalServiceTest {

    private static final String EMBETE_KODEREF = "FMHO";
    private static final String EMBETE_DECODE = "Fylkesmannen i Hordaland";

    private static final String MANDATTYPE_KODEREF = "FIN";
    private static final String MANDATTYPE_DECODE = "Ivareta personens interesser innenfor det økonomiske området";

    private static final String SAKSTYPE_KODEREF = "ANN";
    private static final String SAKSTYPE_DECODE = "Forvaltning utenfor vergemål";

    private static final String VERGETYPE_KODEREF = "ADV";
    private static final String VERGETYPE_DECODE = "Advokat";

    private VergemalService vergemalService;
    private PersonV3 personV3Mock;
    private KodeverkmanagerBi kodeverkManger;
    private DefaultPersonKjerneinfoService personServiceMock;

    @BeforeEach
    void beforeEach() {
        personV3Mock = mock(PersonV3.class);
        personServiceMock = mock(DefaultPersonKjerneinfoService.class);
        kodeverkManger = mockKodeverk();

        vergemalService = new VergemalService(personV3Mock, personServiceMock, kodeverkManger);
    }

    private KodeverkmanagerBi mockKodeverk() {
        KodeverkmanagerBi mock = mock(KodeverkmanagerBi.class);
        try {
            when(mock.getBeskrivelseForKode(EMBETE_KODEREF, KODEVERKSREF_VERGEMAL_FYLKESMANSSEMBETER, "nb"))
                    .thenReturn(EMBETE_DECODE);
            when(mock.getBeskrivelseForKode(MANDATTYPE_KODEREF, KODEVERKSREF_VERGEMAL_MANDATTYPE, "nb"))
                    .thenReturn(MANDATTYPE_DECODE);
            when(mock.getBeskrivelseForKode(SAKSTYPE_KODEREF, KODEVERKSREF_VERGEMAL_SAKSTYPE, "nb"))
                    .thenReturn(SAKSTYPE_DECODE);
            when(mock.getBeskrivelseForKode(VERGETYPE_KODEREF, KODEVERKSREF_VERGEMAL_VERGETYPE, "nb"))
                    .thenReturn(VERGETYPE_DECODE);
        } catch (HentKodeverkKodeverkIkkeFunnet hentKodeverkKodeverkIkkeFunnet) {
            throw new RuntimeException(hentKodeverkKodeverkIkkeFunnet);
        }
        return mock;
    }

    @Test
    @DisplayName("For personer uten vergemål")
    void utenVergemal() throws HentVergeSikkerhetsbegrensning, HentVergePersonIkkeFunnet {
        when(personV3Mock.hentVerge(any())).thenReturn(new WSHentVergeResponse());

        List<Verge> vergemal = vergemalService.hentVergemal("123");

        assertEquals(0, vergemal.size());
    }

    @Nested()
    @DisplayName("Med vergemål")
    class MedVergemal {

        private static final String VERGES_IDENT = "123";
        private static final String VERGES_NAVN = "Arne";

        @BeforeEach
        void beforeEach() throws HentVergeSikkerhetsbegrensning, HentVergePersonIkkeFunnet {
            when(personV3Mock.hentVerge(any())).thenReturn(mockResponsMedVerger());
            when(personServiceMock.hentKjerneinformasjon(any())).thenReturn(mockPersoninformasjonForVerge());
        }

        @Test
        @DisplayName("Henter informasjon om verge fra TPS")
        void medVergemal() {
            List<Verge> vergemal = vergemalService.hentVergemal(VERGES_IDENT);
            Verge verge = vergemal.get(0);

            assertEquals(VERGES_IDENT, verge.getIdent());
            assertEquals(VERGES_NAVN, verge.getPersonnavn().getSammensattNavn());
        }

        @Test
        @DisplayName("Henter kodeverksverdier fra kodeverket")
        void henterKodeverksverdier() {
            List<Verge> vergemal = vergemalService.hentVergemal(VERGES_IDENT);
            Verge verge = vergemal.get(0);

            assertAll("kodeverksverdier",
                    () -> assertEquals(EMBETE_KODEREF, verge.getEmbete().getKodeRef()),
                    () -> assertEquals(EMBETE_DECODE, verge.getEmbete().getBeskrivelse()),
                    () -> assertEquals(SAKSTYPE_KODEREF, verge.getVergesakstype().getKodeRef()),
                    () -> assertEquals(SAKSTYPE_DECODE, verge.getVergesakstype().getBeskrivelse()),
                    () -> assertEquals(VERGETYPE_KODEREF, verge.getVergetype().getKodeRef()),
                    () -> assertEquals(VERGETYPE_DECODE, verge.getVergetype().getBeskrivelse()),
                    () -> assertEquals(MANDATTYPE_KODEREF, verge.getMandattype().getKodeRef()),
                    () -> assertEquals(MANDATTYPE_DECODE, verge.getMandattype().getBeskrivelse()));
        }

        @Test
        @DisplayName("Vergemål har med begrunnelse i kall mot kjerneinfo by default.")
        void medBegrunnelse() {
            ArgumentCaptor<HentKjerneinformasjonRequest> argumentCaptor = ArgumentCaptor.forClass(HentKjerneinformasjonRequest.class);
            when(personServiceMock.hentKjerneinformasjon(argumentCaptor.capture())).thenReturn(mockPersoninformasjonForVerge());

            vergemalService.hentVergemal(VERGES_IDENT);
            HentKjerneinformasjonRequest captured = argumentCaptor.getValue();

            assertTrue(captured.isBegrunnet());
        }


        private WSHentVergeResponse mockResponsMedVerger() {
            return new WSHentVergeResponse()
                    .withVergeListe(getVergeMock(VERGES_IDENT));
        }

        private HentKjerneinformasjonResponse mockPersoninformasjonForVerge() {
            Person person = new Person();
            person.setPersonfakta(mockVergesPersonfakta());
            person.setFodselsnummer(new Fodselsnummer(VERGES_IDENT));
            HentKjerneinformasjonResponse hentKjerneinformasjonResponse = new HentKjerneinformasjonResponse();
            hentKjerneinformasjonResponse.setPerson(person);
            return hentKjerneinformasjonResponse;
        }

        private Personfakta mockVergesPersonfakta() {
            Personfakta personfakta = new Personfakta();
            Personnavn personnavn = new Personnavn();
            personnavn.setSammensattNavn(VERGES_NAVN);
            personfakta.setPersonnavn(personnavn);
            return personfakta;
        }
    }

    @Nested()
    @DisplayName("Med vergemål med ufullstendig data")
    class MedVergemalMedDarligDatakvalitet {

        @BeforeEach
        void beforeEach() throws HentVergeSikkerhetsbegrensning, HentVergePersonIkkeFunnet {
            when(personV3Mock.hentVerge(any())).thenReturn(new WSHentVergeResponse()
                    .withVergeListe(getVergeMock(TPS_VERGES_FNR_MANGLENDE_DATA)));
            when(personServiceMock.hentKjerneinformasjon(any()))
                    .thenThrow(new RuntimeException("TPS Ble kalt med ugyldig fødselsnummer"));

        }

        @Test
        @DisplayName("Verges fødselsnummer satt til 0")
        void medVergemal() {
            List<Verge> vergemal = vergemalService.hentVergemal(TPS_VERGES_FNR_MANGLENDE_DATA);

            assertEquals(null, vergemal.get(0).getIdent());
            assertEquals(null, vergemal.get(0).getPersonnavn());
        }

    }

    private WSVerge getVergeMock(String ident) {
        return new WSVerge()
                .withEmbete(new WSFylkesmannsembete().withValue(EMBETE_KODEREF))
                .withMandattype(new WSMandattyper().withValue(MANDATTYPE_KODEREF))
                .withVergesakstype(new WSVergesakstyper().withValue(SAKSTYPE_KODEREF))
                .withVirkningsperiode(new WSPeriode())
                .withVergetype(new WSVergetyper().withValue(VERGETYPE_KODEREF))
                .withVerge(new WSPersonIdent()
                        .withIdent(new WSNorskIdent().withIdent(ident)));
    }

}