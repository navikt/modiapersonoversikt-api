package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.consume;

import no.nav.sbl.dialogarena.mottaksbehandling.ISvar;
import no.nav.sbl.dialogarena.mottaksbehandling.Mottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.context.MottaksbehandlingKontekst;
import no.nav.sbl.dialogarena.mottaksbehandling.ko.HendelseKo;
import no.nav.sbl.dialogarena.mottaksbehandling.ko.HendelseKoStub;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.HenvendelseRepoStub;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Oppgavesystem;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.OppgavesystemStub;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BesvareTraadTest {

    @Mock
    private Mottaksbehandling mottaksbehandling;

    private Traader service;

    private HenvendelseRepoStub repo = new HenvendelseRepoStub();

    @Before
    public void wireUpService() {
        service = new Traader(mottaksbehandling, null);
    }

    @Test
    public void leggerInnSvarIEnTraad() {
        Traad traad = new Traad(Tema.HJELPEMIDLER, "svar-42");
        traad.getSvar().fritekst = "Denne osten stinker";
        traad.erSensitiv = true;
        traad.getSvar().tema = Tema.FAMILIE_OG_BARN;


        ArgumentCaptor<Record<ISvar>> svar = (ArgumentCaptor<Record<ISvar>>) ArgumentCaptor.forClass(new Record<ISvar>().getClass());
        service.besvareSporsmal(traad);
        verify(mottaksbehandling).besvarSporsmal(svar.capture());

        assertThat(svar.getValue().get(ISvar.behandlingsId), is("svar-42"));
        assertThat(svar.getValue().get(ISvar.fritekst), is("Denne osten stinker"));
        assertThat(svar.getValue().get(ISvar.tema), is(Tema.FAMILIE_OG_BARN));
        assertTrue(svar.getValue().get(ISvar.sensitiv));


    }
}
