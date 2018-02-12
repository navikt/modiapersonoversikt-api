package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.wicket.test.EventGenerator;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.DELVIS_SVAR_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(createMelding("id1", SPORSMAL_SKRIFTLIG, now(), Temagruppe.ARBD, "id1")));
        wicket.goToPageWith(new MeldingerWidget("meldinger", "M", "fnr"));
    }

    @Test
    public void reagererPaaEvent() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(createMelding("id1", SPORSMAL_SKRIFTLIG, now(), Temagruppe.ARBD, "id1")));
        wicket.goToPageWith(new MeldingerWidget("meldinger", "M", "fnr"))
                .sendEvent(new EventGenerator() {
                    @Override
                    public Object createEvent(AjaxRequestTarget target) {
                        return Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER;
                    }
                })
                .should().inAjaxResponse().haveComponents(ofType(MeldingerWidget.class));
    }

    @Test
    public void sorteresRiktig() {
        when(henvendelseBehandlingService.hentMeldinger(anyString())).thenReturn(asList(
                createMelding("id1", SPORSMAL_SKRIFTLIG, DateTime.parse("2017-09-01"), Temagruppe.ARBD, "id1"),
                createMelding("id2", SPORSMAL_SKRIFTLIG, DateTime.parse("2017-10-01"), Temagruppe.ARBD, "id2"),
                createMelding("id3", SPORSMAL_SKRIFTLIG, DateTime.parse("2017-11-01"), Temagruppe.ARBD, "id3")
        ));

        assertThat(new MeldingerWidget("meldinger", "M", "fnr").getFeedItems().stream()
                .map(WidgetMeldingVM::getId).collect(toList()),
                is(asList("id3", "id2", "id1")));
    }

}