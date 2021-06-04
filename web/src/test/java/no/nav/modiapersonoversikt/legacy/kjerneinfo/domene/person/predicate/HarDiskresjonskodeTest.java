package no.nav.modiapersonoversikt.legacy.kjerneinfo.domene.person.predicate;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Person;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Personfakta;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Familierelasjonstype;
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.predicate.HarDiskresjonskode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class HarDiskresjonskodeTest {

    private static List<Familierelasjon> familierelasjonList;
    private static HarDiskresjonskode kode;
    private static Familierelasjon relasjon;
    private static Person person = new Person.With().fodselsnummer("01011050243").done();
    private static Personfakta fakta = new Personfakta();

    @Before
    public void setUp() {
        familierelasjonList = new ArrayList<>();
        relasjon = new Familierelasjon();
    }

    @Test
    public void testHarDiskresjonskodeKonstrutoer() {
        try {
            kode = new HarDiskresjonskode(null);
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            kode = new HarDiskresjonskode(Familierelasjonstype.SAMBOER);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testHarDiskresjonskode() {
        assertFalse(new HarDiskresjonskode(Familierelasjonstype.GIFT).test(null));
        assertFalse(new HarDiskresjonskode(Familierelasjonstype.GIFT).test(new Familierelasjon()));
        Familierelasjon tomRelasjon = new Familierelasjon();
        tomRelasjon.setTilPerson(new Person.With().done());
        assertFalse(new HarDiskresjonskode(Familierelasjonstype.GIFT).test(tomRelasjon));


        assertFalse(matchesPredicate(familierelasjonList, new HarDiskresjonskode(Familierelasjonstype.GIFT)));
        person.setPersonfakta(fakta);
        assertFalse(matchesPredicate(familierelasjonList, new HarDiskresjonskode(Familierelasjonstype.GIFT)));
        relasjon.setTilPerson(person);
        assertFalse(matchesPredicate(familierelasjonList, new HarDiskresjonskode(Familierelasjonstype.GIFT)));
        relasjon.setTilRolle(Familierelasjonstype.GIFT.name());
        assertFalse(matchesPredicate(familierelasjonList, new HarDiskresjonskode(Familierelasjonstype.GIFT)));
        fakta.setDiskresjonskode(new Kodeverdi("SPFO", "Sperret adresse, fortrolig"));
        familierelasjonList.add(relasjon);
        assertTrue(matchesPredicate(familierelasjonList, new HarDiskresjonskode(Familierelasjonstype.GIFT)));
        assertFalse(matchesPredicate(familierelasjonList, new HarDiskresjonskode(Familierelasjonstype.SAMBOER)));
    }

    private boolean matchesPredicate(List<Familierelasjon> liste, Predicate<Familierelasjon> test) {
        return liste
                .stream()
                .anyMatch(test);
    }
}