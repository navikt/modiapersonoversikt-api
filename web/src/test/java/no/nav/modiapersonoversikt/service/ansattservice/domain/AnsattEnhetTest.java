package no.nav.modiapersonoversikt.service.ansattservice.domain;


import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AnsattEnhetTest {

    @Test
    public void erAktivReturnererTrueDersomEnhetErAktivIUppercase() {
        final AnsattEnhet ansattEnhet = new AnsattEnhet("1", "NAV Testenheten", "AKTIV");

        assertThat(ansattEnhet.erAktiv(), is(true));
    }

    @Test
    public void erAktivReturnererTrueDersomEnhetErAktivILowercase() {
        final AnsattEnhet ansattEnhet = new AnsattEnhet("1", "NAV Testenheten", "aktiv");

        assertThat(ansattEnhet.erAktiv(), is(true));
    }

    @Test
    public void erAktivReturnererFalseDersomEnhetIkkeErAktiv() {
        final AnsattEnhet ansattEnhet = new AnsattEnhet("1", "NAV Testenheten", "slettesIkkeAktiv");

        assertThat(ansattEnhet.erAktiv(), is(false));
    }
}
