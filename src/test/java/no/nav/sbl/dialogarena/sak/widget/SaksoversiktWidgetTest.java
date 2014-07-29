package no.nav.sbl.dialogarena.sak.widget;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
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

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class SaksoversiktWidgetTest extends AbstractWicketTest {

    @Inject
    private SaksoversiktService saksoversiktService;

    @Before
    public void setup() {
    }

    @Test
    public void skalViseSaker() {
        TemaVM temaVM = new TemaVM().withTemaKode("AAP").withSistOppdaterteBehandling(new GenerellBehandling().withBehandlingsDato(DateTime.now()));
        when(saksoversiktService.hentTemaer(anyString())).thenReturn(asList(temaVM));

        SaksoversiktWidget widget = new SaksoversiktWidget("saksoversikt", "", "");
        wicketTester.goToPageWith(widget);

        wicketTester.should().containPatterns("AAP");
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
        when(saksoversiktService.hentTemaer(anyString())).thenThrow(new SystemException("You messed up, man", new RuntimeException()));

        SaksoversiktWidget widget = new SaksoversiktWidget("saksoversikt", "", "");
        wicketTester.goToPageWith(widget);

        wicketTester.should().containPatterns("kan ikke vise saker");
    }
}
