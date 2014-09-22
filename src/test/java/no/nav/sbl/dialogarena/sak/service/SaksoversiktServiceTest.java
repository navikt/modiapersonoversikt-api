package no.nav.sbl.dialogarena.sak.service;

import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingsstegtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.createWSBehandlingskjede;
import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.createWSSak;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.FERDIG;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseStatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType.DOKUMENTINNSENDING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSInnsendingsvalg.INNSENDT;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad.Dokumentforventninger;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaksoversiktServiceTest {

    private static final String DAG = "DAG";
    private static final String HJL = "HJL";
    private static final String IKKE_KVITTERING = "ikke-kvittering";
    private static final String KVITTERING = "kvittering";
    private static final String BEHANDLINGSKJEDEID_1 = "behandlingskjedeid-1";
    private static final DateTime MERGET_OPPRETTET = new DateTime().minusDays(110);
    public static final String BEHANDLINGSTEMA = "behandlingstema";

    @Mock
    SakOgBehandling_v1PortType sakOgBehandling;

    @Mock
    HenvendelseSoknaderPortType henvendelse;

    @Mock
    AktoerPortType aktoer;

    @Mock
    private SakOgBehandlingFilter sakOgBehandlingFilter = new SakOgBehandlingFilter();

    @InjectMocks
    private SaksoversiktService service;

    private FinnSakOgBehandlingskjedeListeResponse saker = new FinnSakOgBehandlingskjedeListeResponse();
    private List<WSSoknad> henvendelseSoknader = new ArrayList<>();

    @Before
    public void setup() {
        HentAktoerIdForIdentResponse aktoerResponse = new HentAktoerIdForIdentResponse();
        aktoerResponse.setAktoerId("aktor");
        when(sakOgBehandling.finnSakOgBehandlingskjedeListe(any(FinnSakOgBehandlingskjedeListeRequest.class)))
                .thenReturn(saker);
        when(henvendelse.hentSoknadListe(any(String.class)))
                .thenReturn(henvendelseSoknader);
        try {
            when(aktoer.hentAktoerIdForIdent(any(HentAktoerIdForIdentRequest.class)))
                    .thenReturn(aktoerResponse);
        } catch (Exception e) {
            //whatever
        }
        when(sakOgBehandlingFilter.filtrerSaker(anyListOf(WSSak.class))).thenAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArguments()[0]; // Filtrerer ingenting og returnerer argumentet
            }
        });
    }

    @Test
    public void hentTemaer_skalGi_likeMangeTemaSomSaker() {
        saker.withSak(createWSSak(), createWSSak(), createWSSak());
        assertThat(service.hentTemaer("abc").size(), equalTo(saker.getSak().size()));
    }

    @Test
    public void hentTemaer_skalSortere_omvendtKronologisk() {
        WSSak oldSak = createWSSakMinusYear(1);
        WSSak newSak = createWSSakMinusYear(0);
        saker.withSak(oldSak, newSak);
        List<TemaVM> temaer = service.hentTemaer("abc");
        assertTrue(temaer.get(0).sistoppdaterteBehandling.behandlingDato.isAfter(temaer.get(1).sistoppdaterteBehandling.behandlingDato));
    }

    @Test
    public void hentBehandlingerForTemakode_skalMerge_fraBeggeBaksystemer() {
        saker.withSak(opprettSakOgBehandlingGrunnlag());
        henvendelseSoknader = opprettHenvendelseGrunnlag();
        when(henvendelse.hentSoknadListe(anyString())).thenReturn(henvendelseSoknader);

        // TODO: Fiks denne testen før merge med develop - NPE
        List<GenerellBehandling> behandlingerFraTemaKodeDag = service.hentFiltrerteBehandlingerForTemakode("213454 12312", DAG);
        Kvittering kvittering = (Kvittering) behandlingerFraTemaKodeDag.iterator().next();

        assertThat(kvittering.innsendteDokumenter.size(), equalTo(4));
    }

    @Test
    public void hentBehandlingerForTemakode_skalSortere_omvendtKronologisk() {
        String kronotema = "kronotema";
        WSSak sak = createWSSak().withSakstema(new WSSakstemaer().withValue(kronotema));
        sak.withBehandlingskjede(
                createWSBehandlingskjede().withSlutt(now().minusYears(3)),
                createWSBehandlingskjede().withSlutt(now().minusYears(2)),
                createWSBehandlingskjede().withSlutt(now().minusYears(1))
        );
        saker.withSak(sak);
        // TODO: Fiks denne testen før merge med develop - NPE
        List<GenerellBehandling> behandlinger = service.hentFiltrerteBehandlingerForTemakode("123123123", kronotema);
        assertTrue(behandlinger.get(0).behandlingDato.isAfter(behandlinger.get(1).behandlingDato));
    }

    @Test
    public void hentAlleBehandlingerSkalReturnereHenvendelserForAlleTema() {
        String syk = "SYK";
        String oms = "OMS";
        String aap = "AAP";

        saker.withSak(
                createWSSak().withSakstema(new WSSakstemaer().withValue(syk)),
                createWSSak().withSakstema(new WSSakstemaer().withValue(oms)),
                createWSSak().withSakstema(new WSSakstemaer().withValue(aap)),
                createWSSak().withSakstema(new WSSakstemaer().withValue(syk)),
                createWSSak().withSakstema(new WSSakstemaer().withValue(aap)),
                createWSSak().withSakstema(new WSSakstemaer().withValue(syk))
        );

        // TODO: Fiks denne testen før merge med develop - NPE
        Map<TemaVM, List<GenerellBehandling>> behandlinger = service.hentAlleBehandlinger("123123123");
        assertThat(behandlinger.keySet().size(), equalTo(3));
    }

    private List<WSSoknad> opprettHenvendelseGrunnlag() {
        return asList(
                new WSSoknad()
                        .withBehandlingsId(KVITTERING)
                        .withBehandlingsKjedeId(BEHANDLINGSKJEDEID_1)
                        .withEttersending(false)
                        .withHenvendelseType(DOKUMENTINNSENDING.value())
                        .withHenvendelseStatus(FERDIG.value())
                        .withOpprettetDato(MERGET_OPPRETTET)
                        .withInnsendtDato(new DateTime())
                        .withHovedskjemaKodeverkId("hovedskjema")
                        .withDokumentforventninger(
                                new Dokumentforventninger()
                                        .withDokumentforventning(
                                                new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("dokinn-id").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("dokinn-id").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("dokinn-id").withTilleggsTittel("abc"),
                                                new WSDokumentforventning().withInnsendingsvalg(INNSENDT.value()).withKodeverkId("dokinn-id").withTilleggsTittel("abc")
                                        )),
                new WSSoknad().withHenvendelseStatus(UNDER_ARBEID.value()).withBehandlingsId(IKKE_KVITTERING),
                new WSSoknad().withHenvendelseStatus(UNDER_ARBEID.value()).withBehandlingsId(IKKE_KVITTERING)
        );
    }

    private List<WSSak> opprettSakOgBehandlingGrunnlag() {
        DateTime value = new DateTime().withYear(2013);
        return asList(
                new WSSak().withSakstema(new WSSakstemaer().withValue(DAG))
                        .withBehandlingskjede(new WSBehandlingskjede()
                                .withStart(value)
                                .withBehandlingskjedeId("behandlingskjedeid")
                                .withBehandlingstema(new WSBehandlingstemaer().withValue(BEHANDLINGSTEMA))
                                .withStart(MERGET_OPPRETTET)
                                .withBehandlingsListeRef(KVITTERING, IKKE_KVITTERING, IKKE_KVITTERING)
                        ),
                new WSSak().withSakstema(new WSSakstemaer().withValue(HJL))
        );
    }

    private WSSak createWSSakMinusYear(int i) {
        return new WSSak()
                .withSaksId("saksId-mock")
                .withSakstema(new WSSakstemaer().withValue("DAG").withKodeverksRef("kodeverk-ref-mock"))
                .withBehandlingskjede(
                        new WSBehandlingskjede()
                                .withBehandlingskjedeId("behandlingskjedeid-mock")
                                .withBehandlingstema(new WSBehandlingstemaer().withKodeverksRef("kodeverk-ref-mock"))
                                .withSisteBehandlingREF("siste-behandling-ref-mock")
                                .withSisteBehandlingstype(new WSBehandlingstyper().withKodeverksRef("behandlingstype-ref-mock"))
                                .withSisteBehandlingsstegREF("siste-behandling-steg-ref-mock")
                                .withSisteBehandlingsstegtype(new WSBehandlingsstegtyper().withKodeverksRef("behandlingssteg-ref-mock"))
                                .withStart(now().minusYears(i).minusDays(1))
                                .withSlutt(now().minusYears(i)));
    }

}
