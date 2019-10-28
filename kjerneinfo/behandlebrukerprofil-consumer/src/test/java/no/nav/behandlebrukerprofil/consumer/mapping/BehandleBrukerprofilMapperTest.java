package no.nav.behandlebrukerprofil.consumer.mapping;

import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.behandlebrukerprofil.consumer.support.mapping.BehandleBrukerprofilMapper;
import no.nav.behandlebrukerprofil.consumer.support.mock.BehandleBrukerprofilMockFactory;
import no.nav.brukerprofil.domain.Bankkonto;
import no.nav.brukerprofil.domain.BankkontoUtland;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.brukerprofil.domain.adresser.*;
import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.meldinger.FimOppdaterKontaktinformasjonOgPreferanserRequest;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BehandleBrukerprofilMapperTest {

    private BehandleBrukerprofilMapper mapper = BehandleBrukerprofilMapper.getInstance();

    @Test
    public void telefonnummerMappesRiktig() throws Exception {
        final Bruker bruker = BehandleBrukerprofilMockFactory.getBruker();
        BehandleBrukerprofilRequest request = new BehandleBrukerprofilRequest(bruker);
        final FimOppdaterKontaktinformasjonOgPreferanserRequest mapped = mapper.map(request, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);

        final List<FimTelefonnummer> kontaktinformasjon = ((FimBruker) mapped.getBruker()).getKontaktinformasjon();
        assertEquals(kontaktinformasjon.size(), 3);
        sjekkTelefonnumre(kontaktinformasjon, bruker);
    }

    @Test
    public void testRequestMapperWithNorskBankkonto() {
        BehandleBrukerprofilRequest request = new BehandleBrukerprofilRequest(BehandleBrukerprofilMockFactory.getBruker());
        FimOppdaterKontaktinformasjonOgPreferanserRequest fimRequest = mapper.map(request, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);
        verifyFimPerson(fimRequest.getBruker(), request.getBruker());
        verifyFimBruker(fimRequest.getBruker(), request.getBruker());
    }

    @Test
    public void testBankkontoUtland() {
        BankkontoUtland expected = BehandleBrukerprofilMockFactory.getBankkontoUtland();
        FimBankkontoUtland result = mapper.map(expected, FimBankkontoUtland.class);

        verifyFimBankKontoUtland(result, expected);
    }

    @Test
    public void testMapBrukerToFimBrukerdresseToFimMidlertidigPostAdresse() {
        BehandleBrukerprofilRequest request = new BehandleBrukerprofilRequest(BehandleBrukerprofilMockFactory.getBruker());
        verifyAdresseTyper(request);
        verifyAdresseNorgeOgUtland(request);
    }

    @Test
    public void testMapTilrettelagtkommunkasjon() {
        BehandleBrukerprofilRequest from = new BehandleBrukerprofilRequest(BehandleBrukerprofilMockFactory.getBruker());

        FimOppdaterKontaktinformasjonOgPreferanserRequest to = mapper.map(from, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);
        List<FimTilrettelagtKommunikasjon> behov = ((FimBruker) to.getBruker()).getTilrettelagtKommunikasjon();

        assertThat(behov.size(), is(1));
        assertThat(behov.get(0).getBehov(), is(BehandleBrukerprofilMockFactory.TILRETTELAGT_KOMMUNIKASJON_KODE));
    }

    private void verifyFimPerson(FimPerson fimPerson, Bruker bruker) {
        verifyFimNorskIdent(fimPerson.getIdent(), bruker.getIdent());
    }

    private void verifyFimBruker(FimPerson fimPerson, Bruker bruker) {
        FimBruker fimBruker = (FimBruker) fimPerson;
        verifyFimBankKonto(fimBruker.getBankkonto(), bruker.getBankkonto());
        assertEquals(fimBruker.getClass(), FimBruker.class);
        vertifyFimMidlertidigPostadresseNorge(((FimMidlertidigPostadresseNorge) fimBruker.getMidlertidigPostadresse()).getStrukturertAdresse(), bruker.getMidlertidigadresseNorge());
        mapper.map(bruker, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);

    }

    private void verifyFimBankKonto(FimBankkonto fimBankkonto, Bankkonto bankkonto) {
        FimBankkontoNorge fimBankkontoNorge = (FimBankkontoNorge) fimBankkonto;

        assertEquals(bankkonto.getKontonummer(), fimBankkontoNorge.getBankkonto().getBankkontonummer());
        assertEquals(bankkonto.getBanknavn(), fimBankkontoNorge.getBankkonto().getBanknavn());
    }

    private void verifyFimNorskIdent(FimNorskIdent result, String expected) {
        assertEquals(expected, result.getIdent());
    }

    private void verifyFimBankKontoUtland(FimBankkontoUtland result, BankkontoUtland expected) {
        assertEquals(expected.getBankkode(), result.getBankkontoUtland().getBankkode());
        assertEquals(expected.getBanknavn(), result.getBankkontoUtland().getBanknavn());
        assertEquals(expected.getKontonummer(), result.getBankkontoUtland().getBankkontonummer());
        assertEquals(expected.getSwift(), result.getBankkontoUtland().getSwift());
        verifyUstrukturertAdresse(expected.getBankadresse(), result.getBankkontoUtland().getBankadresse());
        verifyKodeverdi(expected.getLandkode(), result.getBankkontoUtland().getLandkode());
        verifyKodeverdi(expected.getValuta(), result.getBankkontoUtland().getValuta());
    }

    private void verifyKodeverdi(Kodeverdi expected, FimKodeverdi result) {
        assertEquals(expected.getKodeRef(), result.getValue());
    }

    private void verifyUstrukturertAdresse(UstrukturertAdresse expected, FimUstrukturertAdresse result) {
        assertEquals(expected.getAdresselinje1(), result.getAdresselinje1());
        assertEquals(expected.getAdresselinje2(), result.getAdresselinje2());
        assertEquals(expected.getAdresselinje3(), result.getAdresselinje3());
        verifyKodeverdi(expected.getLandkode(), result.getLandkode());
    }

    private void vertifyFimMidlertidigPostadresseNorge(FimStrukturertAdresse result, StrukturertAdresse expected) {
        assertEquals(expected.getTilleggsadresseType(), result.getTilleggsadresseType());
        assertEquals(((Gateadresse) expected).getGatenavn(), ((FimGateadresse) result).getGatenavn());
        assertEquals(((Gateadresse) expected).getHusnummer(), ((FimGateadresse) result).getHusnummer().toString());
        assertEquals(((Gateadresse) expected).getBolignummer(), ((FimGateadresse) result).getBolignummer());
    }

    private void vertifyFimMidlertidigPostadresseMatrikkel(FimStrukturertAdresse result, StrukturertAdresse expected) {
        assertEquals(FimMatrikkeladresse.class, result.getClass());
        assertEquals(((Matrikkeladresse) expected).getEiendomsnavn(), ((FimMatrikkeladresse) result).getEiendomsnavn());
        assertEquals(((Matrikkeladresse) expected).getBolignummer(), ((FimMatrikkeladresse) result).getBolignummer());
        assertEquals((expected).getPoststed(), ((FimMatrikkeladresse) result).getPoststed().getValue());
    }

    private void vertifyFimMidlertidigPostboksadresse(FimStrukturertAdresse result, StrukturertAdresse expected) {
        assertEquals(FimPostboksadresseNorsk.class, result.getClass());
        assertEquals(((Postboksadresse) expected).getPostboksnummer().trim(), ((FimPostboksadresseNorsk) result).getPostboksnummer());
        assertEquals(((Postboksadresse) expected).getPostboksanlegg(), ((FimPostboksadresseNorsk) result).getPostboksanlegg());
        assertEquals((expected).getPoststed(), ((FimPostboksadresseNorsk) result).getPoststed().getValue());
    }

    private void verifyAdresseTyper(BehandleBrukerprofilRequest request) {
        FimOppdaterKontaktinformasjonOgPreferanserRequest fimRequest;

        request.getBruker().setMidlertidigadresseNorge(BehandleBrukerprofilMockFactory.createMatrikkeadresse());
        fimRequest = mapper.map(request, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);
        vertifyFimMidlertidigPostadresseMatrikkel(((FimMidlertidigPostadresseNorge) ((FimBruker) fimRequest.getBruker())
                        .getMidlertidigPostadresse()).getStrukturertAdresse(),
                request.getBruker().getMidlertidigadresseNorge());

        request.getBruker().setMidlertidigadresseNorge(BehandleBrukerprofilMockFactory.createPostboksadresse());
        fimRequest = mapper.map(request, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);
        vertifyFimMidlertidigPostboksadresse(((FimMidlertidigPostadresseNorge) ((FimBruker) fimRequest.getBruker())
                        .getMidlertidigPostadresse()).getStrukturertAdresse(),
                request.getBruker().getMidlertidigadresseNorge());
    }

    private void verifyAdresseNorgeOgUtland(BehandleBrukerprofilRequest request) {
        FimOppdaterKontaktinformasjonOgPreferanserRequest fimRequest;

        request.getBruker().setMidlertidigadresseNorge(null);
        request.getBruker().setMidlertidigadresseUtland(BehandleBrukerprofilMockFactory.getUstrukturertAdresse());
        fimRequest = mapper.map(request, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);
        assertEquals(FimMidlertidigPostadresseUtland.class, ((FimBruker) fimRequest.getBruker()).getMidlertidigPostadresse().getClass());

        request.getBruker().setMidlertidigadresseNorge(null);
        request.getBruker().setMidlertidigadresseUtland(null);
        fimRequest = mapper.map(request, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);
        assertNull(((FimBruker) fimRequest.getBruker()).getMidlertidigPostadresse());
        assertNull(((FimBruker) fimRequest.getBruker()).getMidlertidigPostadresse());

        request.getBruker().setMidlertidigadresseNorge(new StrukturertAdresse());
        request.getBruker().setMidlertidigadresseUtland(new UstrukturertAdresse());
        fimRequest = mapper.map(request, FimOppdaterKontaktinformasjonOgPreferanserRequest.class);
        assertEquals(FimMidlertidigPostadresseNorge.class,
                ((FimBruker) fimRequest.getBruker()).getMidlertidigPostadresse().getClass());
    }

    private void sjekkTelefonnumre(List<FimTelefonnummer> telefonnumre, Bruker bruker) {
        for (FimTelefonnummer telefonnummer : telefonnumre) {
            sjekkTelefonnummer(telefonnummer, bruker);
        }
    }

    private void sjekkTelefonnummer(FimTelefonnummer telefon, Bruker bruker) {
        switch (telefon.getType().getValue()) {
            case "MOBI":
                assertEquals(telefon.getIdentifikator(), bruker.getMobil().getIdentifikator());
                break;
            case "HJET":
                assertEquals(telefon.getIdentifikator(), bruker.getHjemTlf().getIdentifikator());
                break;
            case "ARBT":
                assertEquals(telefon.getIdentifikator(), bruker.getJobbTlf().getIdentifikator());
                break;
        }
    }
}
