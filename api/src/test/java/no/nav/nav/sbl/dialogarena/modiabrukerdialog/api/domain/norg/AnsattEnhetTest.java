package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class AnsattEnhetTest {

    @Test
    public void erAktivReturnererTrueDersomEnhetErAktivIUppercase() throws Exception {
        final AnsattEnhet ansattEnhet = new AnsattEnhet("1", "NAV Testenheten", "AKTIV");

        assertThat(ansattEnhet.erAktiv(), is(true));
    }

    @Test
    public void erAktivReturnererTrueDersomEnhetErAktivILowercase() throws Exception {
        final AnsattEnhet ansattEnhet = new AnsattEnhet("1", "NAV Testenheten", "aktiv");

        assertThat(ansattEnhet.erAktiv(), is(true));
    }

    @Test
    public void erAktivReturnererFalseDersomEnhetIkkeErAktiv() throws Exception {
        final AnsattEnhet ansattEnhet = new AnsattEnhet("1", "NAV Testenheten", "slettesIkkeAktiv");

        assertThat(ansattEnhet.erAktiv(), is(false));
    }
}