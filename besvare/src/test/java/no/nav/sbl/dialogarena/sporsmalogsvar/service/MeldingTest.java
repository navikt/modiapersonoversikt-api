package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.joda.time.DateTime;
import org.junit.Test;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class MeldingTest {

    private final DateTime idag0945 = new DateTime(2013, 10, 4, 9, 45);

    @Test
    public void equalsAndHashCodeAreConsistent() {
        EqualsVerifier.forClass(Melding.class).verify();
    }

    @Test
    public void equalsFungererPaaUlikeInstanser() {
        assertThat(new Melding("1", UTGAENDE, idag0945, "x"), equalTo(new Melding("1", UTGAENDE, idag0945, "x")));
        assertThat(new Melding("1", UTGAENDE, idag0945, "x"), not(equalTo(new Melding("1", INNGAENDE, idag0945, "x"))));
        assertThat(new Melding("1", UTGAENDE, idag0945, "x"), not(equalTo(new Melding("1", UTGAENDE, idag0945.minusMillis(1), "x"))));
    }
}
