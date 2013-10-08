package no.nav.sbl.dialogarena.sporsmalogsvar;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Locale;

import static no.nav.sbl.dialogarena.sporsmalogsvar.Datoformat.KORT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.Datoformat.LANGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.Datoformat.MEDIUM;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class DatoformatTest {

    private static final Locale NO = new Locale("nb", "no");

    @Test
    public void langDato() {
        assertThat(Datoformat.format(new DateTime(1981, 6, 4, 12, 15), LANGT, NO), is("torsdag 4. juni 1981, kl 12:15"));
    }

    @Test
    public void mediumDato() {
        assertThat(Datoformat.format(new DateTime(1981, 6, 4, 12, 15), MEDIUM, NO), is("4. jun 1981, kl 12:15"));
    }

    @Test
    public void kortDato() {
        assertThat(Datoformat.format(new DateTime(1981, 6, 4, 12, 15), KORT, NO), is("04.06.1981 kl 12.15"));
    }
}
