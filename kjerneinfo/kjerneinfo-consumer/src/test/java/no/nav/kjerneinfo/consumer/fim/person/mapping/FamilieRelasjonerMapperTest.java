package no.nav.kjerneinfo.consumer.fim.person.mapping;

import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Diskresjonskoder;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.kodeverk.consumer.fim.kodeverk.support.DefaultKodeverkmanager;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse;
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
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon wsFamilierelasjon = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon()
                .withHarSammeBosted(true)
                .withTilPerson(new Bruker());

        Familierelasjon response = mapper.map(wsFamilierelasjon, Familierelasjon.class);

        assertThat(response.getHarSammeBosted(), is(true));
    }

    @Test
    public void barnMedAnnetBosted() {
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon wsFamilierelasjon = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon()
                .withHarSammeBosted(false)
                .withTilPerson(new Bruker());

        Familierelasjon response = mapper.map(wsFamilierelasjon, Familierelasjon.class);

        assertThat(response.getHarSammeBosted(), is(false));
    }

    @Test
    public void barnMedSammeBostedSomNullVerdiDefaulterTilFalse() {
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon wsFamilierelasjon = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon()
                .withHarSammeBosted(null)
                .withTilPerson(new Bruker());

        Familierelasjon response = mapper.map(wsFamilierelasjon, Familierelasjon.class);

        assertThat(response.getHarSammeBosted(), is(false));
    }

    @Test
    public void barnMedDiskresjonskode() {
        no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon wsFamilierelasjon = new no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon()
                .withHarSammeBosted(null)
                .withTilRolle(new no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjoner().withValue("BARN"))
                .withEndringstype(Endringstyper.ENDRET)
                .withTilPerson(mockPersonMedDiskresjonskode());
        HentPersonResponse wsResponse = new HentPersonResponse().withPerson(new Bruker().withHarFraRolleI(wsFamilierelasjon));

        HentKjerneinformasjonResponse response = mapper.map(wsResponse, HentKjerneinformasjonResponse.class);

        Person barn = response.getPerson().getPersonfakta().getHarFraRolleIList().stream().findFirst().get().getTilPerson();
        assertThat(barn.getPersonfakta().getDiskresjonskode().getKodeRef(), is(Diskresjonskoder.FORTROLIG_ADRESSE.getValue()));
        assertThat(barn.getFodselsnummer().getNummer(), is(FODSELSNUMMER));
    }

    private no.nav.tjeneste.virksomhet.person.v3.informasjon.Person mockPersonMedDiskresjonskode() {
        return new no.nav.tjeneste.virksomhet.person.v3.informasjon.Person()
                .withAktoer(getNorskIdent(FODSELSNUMMER))
                .withDiskresjonskode(new no.nav.tjeneste.virksomhet.person.v3.informasjon.Diskresjonskoder().withValue(Diskresjonskoder.FORTROLIG_ADRESSE.getValue()));
    }

    private PersonIdent getNorskIdent(String fodselsnummer) {
        return new PersonIdent().withIdent(new NorskIdent().withIdent(fodselsnummer));
    }

}
