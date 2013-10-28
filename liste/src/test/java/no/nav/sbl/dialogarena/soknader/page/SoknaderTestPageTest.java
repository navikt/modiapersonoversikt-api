package no.nav.sbl.dialogarena.soknader.page;

import no.nav.sbl.dialogarena.soknader.SoknaderTestPage;
import no.nav.sbl.dialogarena.soknader.context.mock.SoknaderMockContext;
import org.apache.wicket.Component;
import org.junit.Test;

import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SoknaderTestPageTest extends AbstractWicketTest {

    @Override
    protected void setup() {
        SoknaderMockContext ctx = new SoknaderMockContext();
        applicationContext.putBean("soknaderService", ctx.soknaderService());
        applicationContext.putBean("aktorService", ctx.aktorService());
    }

    @Test
    public void shouldShowPage() {
        wicketTester.goTo(SoknaderTestPage.class).should().beOn(SoknaderTestPage.class);
    }

    @Test
    public void shouldContainSokandListe() {
        wicketTester.goTo(SoknaderTestPage.class).should().containComponent(withId("soknadListe"));
    }

    @Test
    public void shouldContainFiveSoknader() {
        Component liste = wicketTester.goTo(SoknaderTestPage.class).get().component(withId("soknadListe"));
        List<Component> listitems = wicketTester.get().components(withId("content").and(containedInComponent(equalTo(liste))));
        assertThat(listitems.size(), is(equalTo(5)));
    }

    @Test
    public void thirdSoknadShouldContainCorrectData() {
        Component listItem = getThirdListItemFromFirstListe();
        assertThat(getTextFromListItem("innsendtDato", listItem), is(equalTo("Innsendt 01.10.2013")));
        assertThat(getTextFromListItem("heading", listItem), is(equalTo("Dagpenger")));
        assertThat(getTextFromListItem("behandlingsTid", listItem), is(equalTo("Normert behandlingstid 10 dager")));
        assertThat(getTextFromListItem("status", listItem), is(equalTo("Mottatt")));
        assertThat(getTextFromListItem("status-date", listItem), is(equalTo("01.10.2013")));
    }

    private Component getThirdListItemFromFirstListe() {
        Component liste = wicketTester.goTo(SoknaderTestPage.class).get().component(withId("soknadListe"));
        List<Component> listitems = wicketTester.get().components(withId("content").and(containedInComponent(equalTo(liste))));
        return listitems.get(2);
    }

    private String getTextFromListItem(String id, Component listItem) {
        return wicketTester.get().components(withId(id).and(containedInComponent(equalTo(listItem)))).get(0).getDefaultModelObjectAsString();
    }

}
