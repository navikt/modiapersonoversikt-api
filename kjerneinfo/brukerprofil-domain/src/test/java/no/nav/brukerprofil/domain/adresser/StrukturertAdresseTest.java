package no.nav.brukerprofil.domain.adresser;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


public class StrukturertAdresseTest {

    @Test
    public void setTilleggsadresseUtenCOPrefix() {
        StrukturertAdresse strukturertAdresse = new StrukturertAdresse();

        strukturertAdresse.setTilleggsadresse("Min bostedsadresse");
        assertThat(strukturertAdresse.getTilleggsadresse(), is("Min bostedsadresse"));
    }

    @Test
    public void setTilleggsadresseFjernerCOPrefix() {
        StrukturertAdresse strukturertAdresse = new StrukturertAdresse();

        strukturertAdresse.setTilleggsadresse("C/O Min bostedsadresse");
        assertThat(strukturertAdresse.getTilleggsadresse(), is("Min bostedsadresse"));

        strukturertAdresse.setTilleggsadresse(" C/O Min bostedsadresse");
        assertThat(strukturertAdresse.getTilleggsadresse(), is("Min bostedsadresse"));

        strukturertAdresse.setTilleggsadresse("c/o Min bostedsadresse");
        assertThat(strukturertAdresse.getTilleggsadresse(), is("Min bostedsadresse"));

        strukturertAdresse.setTilleggsadresse("C / O Min bostedsadresse");
        assertThat(strukturertAdresse.getTilleggsadresse(), is("Min bostedsadresse"));
    }

    @Test
    public void setTilleggsadresseMedNullVerdi() {
        StrukturertAdresse strukturertAdresse = new StrukturertAdresse();

        strukturertAdresse.setTilleggsadresse(null);
        assertThat(strukturertAdresse.getTilleggsadresse(), is(nullValue()));
    }

}