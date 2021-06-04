package no.nav.kjerneinfo.domain.person;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kjerneinfo.domain.person.predicate.AdresseUtils;

public class AlternativAdresseUtland extends UstrukturertAdresse {

    private Kodeverdi landkode;
    private Periode postleveringsPeriode;

    @Override
    public String getAdresselinje() {
        StringBuilder adresselinje = new StringBuilder(AdresseUtils.spaceAppend(getAdresselinje1(), getAdresselinje2(), getAdresselinje3(), getAdresselinje4()));

        if (getLandkode() != null && getLandkode().getBeskrivelse() != null) {
            adresselinje.append(" " + getLandkode().getBeskrivelse());
        }
        return adresselinje.toString();
    }

    public Kodeverdi getLandkode() {
        return landkode;
    }

    public void setLandkode(Kodeverdi landkode) {
        this.landkode = landkode;
    }

    public Periode getPostleveringsPeriode() {
        return postleveringsPeriode;
    }

    public void setPostleveringsPeriode(Periode postleveringsPeriode) {
        this.postleveringsPeriode = postleveringsPeriode;
    }
}
