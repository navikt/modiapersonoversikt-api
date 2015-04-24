package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.wicket.test.EventGenerator;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = ServiceTestContext.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MeldingerWidgetTest extends WicketPageTest {

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    @Test
    public void konstrueresRiktig() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(createMelding("id1", SPORSMAL_SKRIFTLIG, now(), "ARBD", "id1")));
        wicket.goToPageWith(new MeldingerWidget("meldinger", "M", "fnr"));
    }

    @Test
    public void reagererPaaEvent() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(createMelding("id1", SPORSMAL_SKRIFTLIG, now(), "ARBD", "id1")));
        wicket.goToPageWith(new MeldingerWidget("meldinger", "M", "fnr"))
                .sendEvent(new EventGenerator() {
                    @Override
                    public Object createEvent(AjaxRequestTarget target) {
                        return MELDING_SENDT_TIL_BRUKER;
                    }
                })
                .should().inAjaxResponse().haveComponents(ofType(MeldingerWidget.class));
    }

}