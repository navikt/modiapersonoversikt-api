package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import java.io.Serializable;
import java.util.List;

public class Saksgruppe implements Serializable {
    public String tema;

    public List<Sak> saksliste;

    public Saksgruppe(String tema, List<Sak> saksliste) {
        this.tema = tema;
        this.saksliste = saksliste;
    }

}


