package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.config.IntegrationTestOverridesConfig;
import no.nav.sbl.dialogarena.sak.config.KodeverkConfig;
import no.nav.sbl.dialogarena.sak.config.SaksoversiktServiceConfig;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSAvslutningsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SaksoversiktServiceConfig.class, KodeverkConfig.class, IntegrationTestOverridesConfig.class})
public class SaksServiceIntegrationTest {

    @Inject
    private SakOgBehandling_v1PortType sakOgBehandlingPortType;

    @Inject
    private HenvendelseSoknaderPortType henvendelseSoknaderPortType;

    @Inject
    private SaksoversiktService saksService;

    @BeforeClass
    public static void setUp() throws Exception {
//        setFrom("environment-test.properties");
//        System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
    }

    @Test
    public void testHentTemaer() throws Exception {
        WSSak wsSakAAP = new WSSak()
                .withSaksId("000232866")
                .withSakstema(new WSSakstemaer().withValue("AAP"))
                .withOpprettet(DateTime.parse("2014-10-10T09:47:43.172+02:00"))
                .withBehandlingskjede(
                        new WSBehandlingskjede()
                                .withBehandlingskjedeId("000QRJA")
                                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("ad0001"))
                                .withStart(DateTime.parse("2014-10-16T14:52:35.938+02:00"))
                                .withSlutt(DateTime.parse("2014-10-16T14:54:18.848+02:00"))
                                .withSluttNAVtid(DateTime.parse("2014-10-16T14:54:18.623+02:00"))
                                .withSisteBehandlingREF("10000HAW7")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0002"))
                                .withBehandlingsListeRef("10000HAW7")
                                .withSisteBehandlingsoppdatering(DateTime.parse("2014-10-16T14:54:18.623+02:00"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                );

        WSSak wsSakDAG = new WSSak()
                .withSaksId("000232866")
                .withSakstema(new WSSakstemaer().withValue("DAG"))
                .withOpprettet(DateTime.parse("2014-09-10T09:47:43.172+02:00"))
                .withBehandlingskjede(
                        new WSBehandlingskjede()
                                .withBehandlingskjedeId("000QRJA")
                                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("ad0001"))
                                .withStart(DateTime.parse("2014-09-16T14:52:35.938+02:00"))
                                .withSlutt(DateTime.parse("2014-09-16T14:54:18.848+02:00"))
                                .withSluttNAVtid(DateTime.parse("2014-09-16T14:54:18.623+02:00"))
                                .withSisteBehandlingREF("10000HAW7")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0002"))
                                .withBehandlingsListeRef("10000HAW7")
                                .withSisteBehandlingsoppdatering(DateTime.parse("2014-09-16T14:54:18.623+02:00"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                );

        when(sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class)))
                .thenReturn(new FinnSakOgBehandlingskjedeListeResponse().withSak(Arrays.asList(wsSakDAG, wsSakAAP)));

        List<TemaVM> tema = saksService.hentTemaer("");

        assertEquals(2, tema.size());
        TemaVM temaAAP = tema.get(0);
        TemaVM temaDAG = tema.get(1);

        assertEquals("AAP", temaAAP.temakode);
        GenerellBehandling sistoppdaterteAAP = temaAAP.sistoppdaterteBehandling;
        assertEquals(DateTime.parse("2014-10-16T14:54:18.848+02:00"), sistoppdaterteAAP.behandlingDato);

        assertEquals("DAG", temaDAG.temakode);
        GenerellBehandling sistoppdaterteDAG = temaDAG.sistoppdaterteBehandling;
        assertEquals(DateTime.parse("2014-09-16T14:54:18.848+02:00"), sistoppdaterteDAG.behandlingDato);
    }

    @Test
    public void testHentBehandlingerForTemakode() throws Exception {
        WSSoknad wsSoknad = new WSSoknad()
                .withBehandlingsId("10000HAW7")
                .withHenvendelseType("SOKNADSINNSENDING")
                .withHovedskjemaKodeverkId("NAV 04-01.03")
                .withHenvendelseStatus("FERDIG")
                .withOpprettetDato(DateTime.parse("2014-09-16T14:52:35.938+02:00"))
                .withEttersending(false)
                .withSistEndretDato(DateTime.parse("2014-09-16T14:54:18.623+02:00"))
                .withInnsendtDato(DateTime.parse("2014-09-16T14:54:18.623+02:00"))
                .withDokumentforventninger(new WSSoknad.Dokumentforventninger().withDokumentforventning(
                        new WSDokumentforventning().withKodeverkId("NAV 04-01.03").withInnsendingsvalg("LASTET_OPP"),
                        new WSDokumentforventning().withKodeverkId("O2").withTilleggsTittel("MMMM").withInnsendingsvalg("LASTET_OPP"),
                        new WSDokumentforventning().withKodeverkId("T8").withTilleggsTittel("MMMM").withInnsendingsvalg("SEND_SENERE")
                ));

        WSSak wsSakAAP = new WSSak()
                .withSaksId("000232866")
                .withSakstema(new WSSakstemaer().withValue("AAP"))
                .withOpprettet(DateTime.parse("2014-10-10T09:47:43.172+02:00"))
                .withBehandlingskjede(
                        new WSBehandlingskjede()
                                .withBehandlingskjedeId("000QRJA")
                                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("ad0001"))
                                .withStart(DateTime.parse("2014-10-16T14:52:35.938+02:00"))
                                .withSluttNAVtid(DateTime.parse("2014-10-16T14:54:18.623+02:00"))
                                .withSisteBehandlingREF("10000XXXX")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0014"))
                                .withBehandlingsListeRef("10000XXXX")
                                .withSisteBehandlingsoppdatering(DateTime.parse("2014-10-16T14:54:18.623+02:00"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("opprettet"))
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                );

        WSSak wsSakDAG = new WSSak()
                .withSaksId("000232866")
                .withSakstema(new WSSakstemaer().withValue("DAG"))
                .withOpprettet(DateTime.parse("2014-09-10T09:47:43.172+02:00"))
                .withBehandlingskjede(
                        new WSBehandlingskjede()
                                .withBehandlingskjedeId("000QRJA")
                                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withValue("ad0001"))
                                .withStart(DateTime.parse("2014-09-16T14:52:35.938+02:00"))
                                .withSlutt(DateTime.parse("2014-09-16T14:54:18.848+02:00"))
                                .withSluttNAVtid(DateTime.parse("2014-09-16T14:54:18.623+02:00"))
                                .withSisteBehandlingREF("10000HAW7")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ae0002"))
                                .withBehandlingsListeRef("10000HAW7")
                                .withSisteBehandlingsoppdatering(DateTime.parse("2014-09-16T14:54:18.623+02:00"))
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue("avsluttet"))
                                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue("ok"))
                );

        when(henvendelseSoknaderPortType.hentSoknadListe(anyString()))
                .thenReturn(Arrays.asList(wsSoknad));
        when(sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class)))
                .thenReturn(new FinnSakOgBehandlingskjedeListeResponse().withSak(Arrays.asList(wsSakDAG, wsSakAAP)));

        Map<TemaVM,List<GenerellBehandling>> temaVMListMap = saksService.hentBehandlingerByTema("");

        Iterator<TemaVM> iterator = temaVMListMap.keySet().iterator();
        List<GenerellBehandling> behandlingerDAG = temaVMListMap.get(iterator.next());
        List<GenerellBehandling> behandlingerAAP = temaVMListMap.get(iterator.next());
        assertEquals(behandlingerAAP.size(), 1);
        assertEquals(behandlingerDAG.size(), 1);
        GenerellBehandling behandlingAAP = behandlingerAAP.get(0);
        GenerellBehandling behandlingDAG = behandlingerDAG.get(0);

        assertEquals(true, behandlingAAP instanceof GenerellBehandling);
        assertEquals(true, behandlingDAG instanceof Kvittering);

        assertEquals(GenerellBehandling.BehandlingsStatus.OPPRETTET, behandlingAAP.behandlingsStatus);
        assertEquals(GenerellBehandling.BehandlingsStatus.AVSLUTTET, behandlingDAG.behandlingsStatus);
    }
}
