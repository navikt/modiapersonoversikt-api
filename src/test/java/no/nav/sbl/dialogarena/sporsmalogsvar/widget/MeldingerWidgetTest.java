package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.wicket.test.EventGenerator;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.createMelding;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = MeldingWidgetTestConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MeldingerWidgetTest extends WicketPageTest {

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Test
    public void skalKonstrueresRiktig() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(createMelding("id1", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "1")));
        wicket.goToPageWith(new TestMeldingerWidget("meldinger", "M", "fnr"));
    }

    @Test
    public void skalInneholdeRiktigAntallMeldinger() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("id1", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "1"),
                createMelding("id2", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "2"),
                createMelding("id3", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "3"),
                createMelding("id4", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "4")
        ));
        wicket.goToPageWith(new TestMeldingerWidget("meldinger", "M", "fnr"))
                .should().containComponents(4, ofType(MeldingWidgetPanel.class));
    }

    @Test
    public void skalInneholdeMaksFemMeldinger() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("id1", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "1"),
                createMelding("id2", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "2"),
                createMelding("id3", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "3"),
                createMelding("id4", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "4"),
                createMelding("id5", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "5"),
                createMelding("id6", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "6")
        ));
        wicket.goToPageWith(new TestMeldingerWidget("meldinger", "M", "fnr"))
                .should().containComponents(5, ofType(MeldingWidgetPanel.class));
    }

    @Test
    public void skalRegerePaaEvent() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(createMelding("id1", Meldingstype.SPORSMAL_SKRIFTLIG, now(), "TEMA", "1")));
        wicket.goToPageWith(new TestMeldingerWidget("meldinger", "M", "fnr"))
                .sendEvent(new EventGenerator() {
                    @Override
                    public Object createEvent(AjaxRequestTarget target) {
                        return MELDING_SENDT_TIL_BRUKER;
                    }
                })
                .should().inAjaxResponse().haveComponents(ofType(MeldingerWidget.class));
    }

}