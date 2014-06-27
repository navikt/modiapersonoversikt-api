package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sakstema implements Serializable {
    public String tema;

    public List<Sak> saksliste;

    public Sakstema(String tema) {
        this.tema = tema;
        saksliste = new ArrayList<>();
    }
}


