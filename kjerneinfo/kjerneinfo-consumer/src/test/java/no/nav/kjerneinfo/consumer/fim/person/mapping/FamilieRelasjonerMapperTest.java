package no.nav.kjerneinfo.consumer.fim.person.mapping;

import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Diskresjonskoder;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentPersonResponse;
import no.nav.tjeneste.virksomhet.person.v3.metadata.Endringstyper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class FamilieRelasjonerMapperTest {

    private static final String FODSELSNUMMER = "10108000398";

    private KjerneinfoMapper mapper;

    @Before
    public void setUp() {
        DefaultKodeverkmanager kodeverk = new DefaultKodeverkmanager(mock(KodeverkPortType.class));
        mapper = new KjerneinfoMapper(kodeverk);
    }

    @Test
    public void barnMedSammeBosted() {
        WSFamilierelasjon wsFamilierelasjon = new WSFamilierelasjon()
                .withHarSammeBosted(true)
                .withTilPerson(new WSBruker());

        Familierelasjon response = mapper.map(wsFamilierelasjon, Familierelasjon.class);

        assertThat(response.getHarSammeBosted(), is(true));
    }

    @Test
    public void barnMedAnnetBosted() {
        WSFamilierelasjon wsFamilierelasjon = new WSFamilierelasjon()
                .withHarSammeBosted(false)
                .withTilPerson(new WSBruker());

        Familierelasjon response = mapper.map(wsFamilierelasjon, Familierelasjon.class);

        assertThat(response.getHarSammeBosted(), is(false));
    }

    @Test
    public void barnMedSammeBostedSomNullVerdiDefaulterTilFalse() {
        WSFamilierelasjon wsFamilierelasjon = new WSFamilierelasjon()
                .withHarSammeBosted(null)
                .withTilPerson(new WSBruker());

        Familierelasjon response = mapper.map(wsFamilierelasjon, Familierelasjon.class);

        assertThat(response.getHarSammeBosted(), is(false));
    }

    @Test
    public void barnMedDiskresjonskode() {
        WSFamilierelasjon wsFamilierelasjon = new WSFamilierelasjon()
                .withHarSammeBosted(null)
                .withTilRolle(new WSFamilierelasjoner().withValue("BARN"))
                .withEndringstype(Endringstyper.ENDRET)
                .withTilPerson(mockPersonMedDiskresjonskode());
        WSHentPersonResponse wsResponse = new WSHentPersonResponse().withPerson(new WSBruker().withHarFraRolleI(wsFamilierelasjon));

        HentKjerneinformasjonResponse response = mapper.map(wsResponse, HentKjerneinformasjonResponse.class);

        Person barn = response.getPerson().getPersonfakta().getHarFraRolleIList().stream().findFirst().get().getTilPerson();
        assertThat(barn.getPersonfakta().getDiskresjonskode().getKodeRef(), is(Diskresjonskoder.FORTROLIG_ADRESSE.getValue()));
        assertThat(barn.getFodselsnummer().getNummer(), is(FODSELSNUMMER));
    }

    private WSPerson mockPersonMedDiskresjonskode() {
        return new WSPerson()
                .withAktoer(getNorskIdent(FODSELSNUMMER))
                .withDiskresjonskode(new WSDiskresjonskoder().withValue(Diskresjonskoder.FORTROLIG_ADRESSE.getValue()));
    }

    private WSPersonIdent getNorskIdent(String fodselsnummer) {
        return new WSPersonIdent().withIdent(new WSNorskIdent().withIdent(fodselsnummer));
    }

}
