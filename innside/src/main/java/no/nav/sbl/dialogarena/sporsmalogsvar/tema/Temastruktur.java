package no.nav.sbl.dialogarena.sporsmalogsvar.tema;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;

public class Temastruktur implements Serializable {
    public String navn;
    public List<String> temaliste;

    public Temastruktur(String navn, String... tema)  {
        this(navn, asList(tema));
    }

    public Temastruktur(String navn, List<String> temaliste) {
        this.navn = navn;
        this.temaliste = temaliste;
    }
}
