package no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal.domain.Verge;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet;
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentNavnBolk;
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentVergePersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentVergeSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentVergeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static java.util.Collections.emptyMap;
import static no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal.VergemalService.TPS_VERGES_FNR_MANGLENDE_DATA;
import static no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager.*;
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
    private PdlOppslagService pdl;
    private KodeverkmanagerBi kodeverkManger;

    @BeforeEach
    void beforeEach() {
        personV3Mock = mock(PersonV3.class);
        pdl = mock(PdlOppslagService.class);
        kodeverkManger = mockKodeverk();

        vergemalService = new VergemalService(personV3Mock, pdl, kodeverkManger);
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
        when(personV3Mock.hentVerge(any())).thenReturn(new HentVergeResponse());

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
            when(pdl.hentNavnBolk(any())).thenReturn(mockPersonnavnForVerge());
        }

        @Test
        @DisplayName("Henter informasjon om verge fra TPS")
        void medVergemal() {
            List<Verge> vergemal = vergemalService.hentVergemal(VERGES_IDENT);
            Verge verge = vergemal.get(0);

            assertEquals(VERGES_IDENT, verge.getIdent());
            assertEquals(VERGES_NAVN, verge.getPersonnavn().getFornavn());
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

        private HentVergeResponse mockResponsMedVerger() {
            return new HentVergeResponse()
                    .withVergeListe(getVergeMock(VERGES_IDENT));
        }

        private HashMap<String, HentNavnBolk.Navn> mockPersonnavnForVerge() {
            return new HashMap<String, HentNavnBolk.Navn>() {{
                put(VERGES_IDENT, new HentNavnBolk.Navn(VERGES_NAVN, null, ""));
            }};
        }
    }

    @Nested()
    @DisplayName("Med vergemål med ufullstendig data")
    class MedVergemalMedDarligDatakvalitet {

        @BeforeEach
        void beforeEach() throws HentVergeSikkerhetsbegrensning, HentVergePersonIkkeFunnet {
            when(personV3Mock.hentVerge(any())).thenReturn(new HentVergeResponse()
                    .withVergeListe(getVergeMock(TPS_VERGES_FNR_MANGLENDE_DATA)));
            when(pdl.hentNavnBolk(any())).thenReturn(emptyMap());

        }

        @Test
        @DisplayName("Verges fødselsnummer satt til 0")
        void medVergemal() {
            List<Verge> vergemal = vergemalService.hentVergemal(TPS_VERGES_FNR_MANGLENDE_DATA);

            assertEquals(null, vergemal.get(0).getIdent());
            assertEquals(null, vergemal.get(0).getPersonnavn());
        }

    }

    private no.nav.tjeneste.virksomhet.person.v3.informasjon.Verge getVergeMock(String ident) {
        return new no.nav.tjeneste.virksomhet.person.v3.informasjon.Verge()
                .withEmbete(new Fylkesmannsembete().withValue(EMBETE_KODEREF))
                .withMandattype(new Mandattyper().withValue(MANDATTYPE_KODEREF))
                .withVergesakstype(new Vergesakstyper().withValue(SAKSTYPE_KODEREF))
                .withVirkningsperiode(new Periode())
                .withVergetype(new Vergetyper().withValue(VERGETYPE_KODEREF))
                .withVerge(new PersonIdent()
                        .withIdent(new NorskIdent().withIdent(ident)));
    }

}
