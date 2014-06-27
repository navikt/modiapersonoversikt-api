package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import java.io.Serializable;
import java.util.List;

public class Saksgruppe implements Serializable {
    public String fagomrade;

    public List<Sak> saksliste;

    public Saksgruppe(String fagomrade, List<Sak> saksliste) {
        this.fagomrade = fagomrade;
        this.saksliste = saksliste;
    }

}


