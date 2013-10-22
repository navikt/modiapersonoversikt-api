package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype;
import org.apache.wicket.model.Model;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class MeldingOverskriftTest {

    private Traad traad;

    @Before
    public void lagTraad() {
        traad = new Traad("OST", "100");
    }



    @Test
    public void traadMedKunEnMeldingHarMeldingsoverskriftMeldingFra() {
        Melding sporsmal = new Melding("1", Meldingstype.INNGAENDE, DateTime.now(), "sprsml");
        traad.leggTil(sporsmal);
        assertThat(sporsmal, harOverskrift(startsWith("Melding fra")));
    }

    @Test
    public void alleMeldingerITraadenHarOverskriftSomBegynnerMedSvarFraUnntattDenForste() {
        Melding sporsmal = new Melding("1", Meldingstype.INNGAENDE, DateTime.now().minusDays(5), "sprsml");
        Melding svar = new Melding("2", Meldingstype.UTGAENDE, DateTime.now().minusDays(4), "svar");
        Melding svar2 = new Melding("3", Meldingstype.UTGAENDE, DateTime.now().minusDays(3), "svar2");
        Melding sporsmal2 = new Melding("4", Meldingstype.UTGAENDE, DateTime.now().minusDays(2), "sprsml2");
        traad.leggTil(asList(sporsmal, svar, svar2, sporsmal2));

        assertThat(sporsmal, harOverskrift(startsWith("Melding fra")));
        assertThat(asList(svar, svar2, sporsmal2), everyItem(harOverskrift(startsWith("Svar fra"))));
    }


    private Matcher<Melding> harOverskrift(final Matcher<String> overskriftMatcher) {
        return new TypeSafeMatcher<Melding>() {

            @Override
            protected void describeMismatchSafely(Melding melding, Description mismatchDescription) {
                mismatchDescription.appendText("overskriften er \"" + overskrift(melding) + "\"");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("overskriften til meldingen er ").appendDescriptionOf(overskriftMatcher);
            }

            @Override
            protected boolean matchesSafely(Melding melding) {
                return overskriftMatcher.matches(overskrift(melding));
            }

            private String overskrift(Melding melding) {
                return new MeldingOverskrift(Model.of(melding), Model.of(traad)).getObject();
            }
        };
    }

}
