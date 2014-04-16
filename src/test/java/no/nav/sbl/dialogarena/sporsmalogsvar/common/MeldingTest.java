package no.nav.sbl.dialogarena.sporsmalogsvar.common;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Test;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.common.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.common.melding.Meldingstype.UTGAENDE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class MeldingTest {

    private final DateTime idag0945 = new DateTime(2013, 10, 4, 9, 45);

    private final Melding melding1 = new Melding("1", INNGAENDE, idag0945.minusDays(2), "x");
    private final Melding melding2 = new Melding("2", INNGAENDE, idag0945.minusDays(1), "y");
    private final Melding melding3 = new Melding("3", UTGAENDE, idag0945, "z");

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

    @Test
    public void tomTraadHarAldriEnInnledendeMelding() {
        Traad traad = new Traad("OST", "100");
        assertThat(asList(melding1, melding2, melding3), everyItem(not(innleder(traad))));
    }

    @Test
    public void avgjoreOmMeldingErForsteIEnTraad() {
        Traad traad = new Traad("OST", "100");
        traad.leggTil(asList(melding3, melding1, melding2));

        assertThat(melding1, innleder(traad));
        assertThat(asList(melding2, melding3), everyItem(not(innleder(traad))));
    }



    private Matcher<Melding> innleder(final Traad traad) {
        return new TypeSafeMatcher<Melding>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("melding som innleder tråden: " + traad);
            }

            @Override
            protected boolean matchesSafely(Melding melding) {
                return melding.innleder(traad);
            }
        };
    }

}
