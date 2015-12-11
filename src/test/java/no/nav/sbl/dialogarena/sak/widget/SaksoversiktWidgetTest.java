package no.nav.sbl.dialogarena.sak.widget;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import no.nav.sbl.dialogarena.sak.service.BulletProofKodeverkService;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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
    public void widgetReturnererSakerFraService() throws Exception {
        String fnr = "11111111111";
        SaksoversiktWidget widget = new SaksoversiktWidget("id", "S", fnr);
        when(saksoversiktService.hentTemaer(fnr)).thenReturn(temaer());

        assertThat(widget.getFeedItems(), is(temaer()));

    }

    private static List<TemaVM> temaer() {
        return asList(tema("AAP"), tema("DAG"), tema("BIL"));
    }

    private static TemaVM tema(String temakode) {
        return new TemaVM().withTemaKode(temakode);
    }
}
