package no.nav.brukerprofil.domain;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class TelefonTest {

    @Test
    public void setterSammenTelefonnummerMedRetningsnummer() {
        Telefon telefon = new Telefon()
                .withRetningsnummer(new Kodeverdi("+47", "NORGE"))
                .withIdentifikator("95959595");

        String sammensattTelefonnummer = telefon.getTelefonnummerMedRetningsnummer();

        assertThat(sammensattTelefonnummer, is("+47 95959595"));
    }

    @Test
    public void setterIkkeSammenTelefonnummerHvisRetningsnummerErNull() {
        Telefon telefon = new Telefon()
                .withIdentifikator("95959595");

        String sammensattTelefonnummer = telefon.getTelefonnummerMedRetningsnummer();

        assertThat(sammensattTelefonnummer, is("95959595"));
    }
}