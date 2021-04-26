package no.nav.kjerneinfo.domain.person.predicate;

import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjon;
import no.nav.kjerneinfo.domain.person.fakta.Familierelasjonstype;

import java.util.function.Predicate;

/**
 * Predikat for evaluering om person har familierelasjoner med diskresjonskode.
 */
public class HarDiskresjonskode implements Predicate<Familierelasjon> {

    private final static String FORTROLIG_ADRESSE = "SPFO";
    private final static String STRENGT_FORTROLIG_ADRESSE = "SPSF";

    private Familierelasjonstype familierelasjonstype;

    public HarDiskresjonskode(Familierelasjonstype familierelasjonstype) {
        if (familierelasjonstype == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.familierelasjonstype = familierelasjonstype;
    }

    @Override
    public boolean test(Familierelasjon familierelasjon) {
        if (familierelasjon == null || familierelasjon.getTilPerson() == null || familierelasjon.getTilPerson().getPersonfakta() == null) {
            return false;
        }

        Personfakta personfakta = familierelasjon.getTilPerson().getPersonfakta();

        String diskresjonskode = getDiskresjonskode(personfakta);

        return familierelasjonstype.name().equals(familierelasjon.getTilRolle())
                && (STRENGT_FORTROLIG_ADRESSE.equals(diskresjonskode) || FORTROLIG_ADRESSE.equals(diskresjonskode));
    }

    private String getDiskresjonskode(Personfakta personfakta) {
        return personfakta.getDiskresjonskode() == null ? "" : personfakta.getDiskresjonskode().getKodeRef();
    }
}
