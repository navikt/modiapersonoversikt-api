package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.TEMA;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.HenvendelseType.DOKUMENTINNSENDING;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class SaksoversiktLerretTest extends AbstractWicketTest {

    private SaksoversiktLerret lerret;

    @Inject
    private SaksoversiktService service;

    @Override
    protected void setup() {
    }

    @Test
    public void skalÅpneLerretUtenFeil() {
        List<GenerellBehandling> behandlinger = asList(lagBehandling());
        when(service.hentTemaer("123")).thenReturn(asList(new TemaVM().withSistOppdaterteBehandling(lagBehandling()).withTemaKode("abc")));
        when(service.hentBehandlingerForTemakode("123", TEMA)).thenReturn(behandlinger);
        lerret = new SaksoversiktLerret("lerret", "123");

        lerret.hentNyeHendelser(TEMA);
        wicketTester.goToPageWith(lerret);
    }

    private GenerellBehandling lagBehandling() {
        return new GenerellBehandling()
                .withOpprettetDato(DateTime.now().minusHours(1))
                .withBehandlingsDato(DateTime.now())
                .withBehandlingStatus(AVSLUTTET)
                .withBehandlingsTema("fjolsetema")
                .withHenvendelseType(DOKUMENTINNSENDING);
    }
}
