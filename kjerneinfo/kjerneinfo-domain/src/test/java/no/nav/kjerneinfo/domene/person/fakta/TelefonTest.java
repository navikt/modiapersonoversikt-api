package no.nav.kjerneinfo.domene.person.fakta;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.domain.person.fakta.Telefon;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TelefonTest {

    private final String IDENTIFIKATOR = "94221919";

    @Test
    public void harIdentifikatorGirFalseUtenIdentifikorSatt() {
        Telefon telefon = new Telefon();

        boolean harIdentifikator = telefon.harIdentifikator();

        assertFalse(harIdentifikator);
    }

    @Test
    public void harIdentifikatorGirFalseMedTomIdentifikor() {
        Telefon telefon = new Telefon();
        telefon.setIdentifikator("");

        boolean harIdentifikator = telefon.harIdentifikator();

        assertFalse(harIdentifikator);
    }

    @Test
    public void harIdentifikatorGirTrueMedIdentifikor() {
        Telefon telefon = new Telefon();
        telefon.setIdentifikator("116123");

        boolean harIdentifikator = telefon.harIdentifikator();

        assertTrue(harIdentifikator);
    }

    @Test
    public void girTelefonnummerMedRetningsnummer() {
        Telefon telefon = new Telefon()
                .withIdentifikator(IDENTIFIKATOR)
                .withRetningsnummer(new Kodeverdi("+47", "Norge"));

        String telefonnummerMedRetningsnummer = telefon.getTelefonnummerMedRetningsnummer();

        assertThat(telefonnummerMedRetningsnummer, is ("+47 " + IDENTIFIKATOR));
    }

    @Test
    public void girTelefonnummerUtenRetningsnummer() {
        Telefon telefon = new Telefon()
                .withIdentifikator(IDENTIFIKATOR);

        String telefonnummerMedRetningsnummer = telefon.getTelefonnummerMedRetningsnummer();

        assertThat(telefonnummerMedRetningsnummer, is (IDENTIFIKATOR));
    }
}
