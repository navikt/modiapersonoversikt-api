package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.modig.wicket.test.EventGenerator;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import javax.inject.Named;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TestUtils.createMelding;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.joda.time.DateTime.now;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = MockServiceTestContext.class)
@ExtendWith(SpringExtension.class)
public class MeldingerWidgetTest extends WicketPageTest {

    @Inject
    @Named("henvendelseBehandlingService")
    private HenvendelseBehandlingService henvendelseBehandlingService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @Test
    public void konstrueresRiktig() {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(createMelding("id1", SPORSMAL_SKRIFTLIG, now(), Temagruppe.ARBD, "id1"))));
        wicket.goToPageWith(new MeldingerWidget("meldinger", "M", "fnr"));
    }

    @Test
    public void reagererPaaEvent() {
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(createMelding("id1", SPORSMAL_SKRIFTLIG, now(), Temagruppe.ARBD, "id1"))));
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
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("0118");
        when(henvendelseBehandlingService.hentMeldinger(anyString(), anyString())).thenReturn(new Meldinger(asList(
                createMelding("id1", SPORSMAL_SKRIFTLIG, DateTime.parse("2017-09-01"), Temagruppe.ARBD, "id1"),
                createMelding("id2", SPORSMAL_SKRIFTLIG, DateTime.parse("2017-10-01"), Temagruppe.ARBD, "id2"),
                createMelding("id3", SPORSMAL_SKRIFTLIG, DateTime.parse("2017-11-01"), Temagruppe.ARBD, "id3")
        )));

        assertThat(new MeldingerWidget("meldinger", "M", "fnr").getFeedItems().stream()
                .map(WidgetMeldingVM::getId).collect(toList()),
                is(asList("id3", "id2", "id1")));
    }

}