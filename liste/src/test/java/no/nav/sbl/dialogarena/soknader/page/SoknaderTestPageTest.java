package no.nav.sbl.dialogarena.soknader.page;

import no.nav.sbl.dialogarena.soknader.SoknaderTestPage;
import no.nav.sbl.dialogarena.soknader.context.mock.SoknaderMockContext;
import org.junit.Test;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

public class SoknaderTestPageTest extends AbstractWicketTest {
    @Override
    protected void setup() {
        SoknaderMockContext ctx = new SoknaderMockContext();
        applicationContext.putBean("soknaderService", ctx.soknaderService());
    }

    @Test
    public void shouldShowPage(){
        wicketTester.goTo(SoknaderTestPage.class).should().beOn(SoknaderTestPage.class);
    }
    @Test
    public void shouldContainSokander(){
        wicketTester.goTo(SoknaderTestPage.class).should().containComponent(withId("soknadListe"));
    }

}
