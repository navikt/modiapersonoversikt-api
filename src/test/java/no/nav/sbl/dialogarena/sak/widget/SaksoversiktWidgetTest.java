package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.core.exception.SystemException;
import no.nav.modig.modia.widget.FeedWidget;
import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class SaksoversiktWidgetTest extends AbstractWicketTest {

    @Inject
    private SaksoversiktService saksoversiktService;

    @Inject
    private BulletProofKodeverkService kodeverk;

    @Before
    public void setup() {
        reset(saksoversiktService);
    }

    @Test
    public void skalKunneAapneSideMedSaker() {
        TemaVM temaVM = new TemaVM().withTemaKode("AAP").withSistOppdaterteBehandling(new GenerellBehandling().withBehandlingsDato(DateTime.now()));
        ArrayList<TemaVM> temaVMs = new ArrayList<>();
        temaVMs.add(temaVM);
        when(saksoversiktService.hentTemaer(anyString())).thenReturn(temaVMs);
        when(kodeverk.getTemanavnForTemakode("AAP", BulletProofKodeverkService.ARKIVTEMA)).thenReturn("Arbeidsavklaringspæng");

        SaksoversiktWidget widget = new SaksoversiktWidget("saksoversikt", "", "");
        wicketTester.goToPageWith(widget).should().containPatterns("Arbeidsavklaringspæng");
    }

    @Test
    public void skalViseMeldingNårNullSaker() {
        when(saksoversiktService.hentTemaer(anyString())).thenReturn(new ArrayList<TemaVM>());

        SaksoversiktWidget widget = new SaksoversiktWidget("saksoversikt", "", "");
        wicketTester.goToPageWith(widget);

        wicketTester.should().containPatterns("finnes ikke noen saker");
    }

    @Test
    public void skalViseMeldingNårFeilPåTjeneste() {
        when(saksoversiktService.hentTemaer(anyString())).thenThrow(new SystemException("You messed up, Holger", new RuntimeException()));

        SaksoversiktWidget widget = new SaksoversiktWidget("saksoversikt", "", "");
        wicketTester.goToPageWith(widget);

        wicketTester.should().containPatterns("kan ikke vise saker");
    }

    @Test
    public void skalViseMeldingVedForMangeSaker() {
        ArrayList<TemaVM> temaVMs = new ArrayList<>();
        for (int x = 0; x < FeedWidget.MAX_NUMBER_OF_FEED_ITEMS + 1; x++) {
            temaVMs.add(new TemaVM().withTemaKode("AAP").withSistOppdaterteBehandling(new GenerellBehandling().withBehandlingsDato(DateTime.now())));
        }

        when(saksoversiktService.hentTemaer(anyString())).thenReturn(temaVMs);

        SaksoversiktWidget widget = new SaksoversiktWidget("saksoversikt", "", "");
        wicketTester.goToPageWith(widget);
        wicketTester.should().containPatterns("2 flere saker.");
    }

    @Test
    public void skalIkkeViseMeldingVedAkkuratNokSaker() {
        ArrayList<TemaVM> temaVMs = new ArrayList<>();
        for (int x = 0; x < FeedWidget.MAX_NUMBER_OF_FEED_ITEMS; x++) {
            temaVMs.add(new TemaVM().withTemaKode("AAP").withSistOppdaterteBehandling(new GenerellBehandling().withBehandlingsDato(DateTime.now())));
        }

        when(saksoversiktService.hentTemaer(anyString())).thenReturn(temaVMs);

        SaksoversiktWidget widget = new SaksoversiktWidget("saksoversikt", "", "");
        wicketTester.goToPageWith(widget);
        wicketTester.should().notContainPatterns("flere saker.");
    }
}
