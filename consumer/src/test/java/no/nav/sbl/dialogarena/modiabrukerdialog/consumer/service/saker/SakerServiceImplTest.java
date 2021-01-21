package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker;


import no.nav.common.log.MDCConstants;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.GsakSakerTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.EndringsInfo;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Fagomradekode;
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sakstypekode;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse;
import org.joda.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.MDC;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class SakerServiceImplTest {
    private static final String FNR = "fnr";
    public static final String SakId_1 = "1";

    @Mock
    private SakV1 sakV1;
    @Mock
    private BehandleSakV1 behandleSak;
    @Mock
    private GsakKodeverk gsakKodeverk;
    @Mock
    private StandardKodeverk standardKodeverk;
    @Mock
    private ArbeidOgAktivitet arbeidOgAktivitet;
    @Mock
    private PsakService psakService;
    @Mock
    private UnleashService unleashService;
    @Mock
    private SakApiGateway sakApiGateway;

    @InjectMocks
    private SakerServiceImpl sakerService;

    @BeforeEach
    void setUp() {
        EnvironmentUtils.setProperty("SAK_ENDPOINTURL", "https://sak-url", EnvironmentUtils.Type.PUBLIC);
        initMocks(this);
        sakerService.setup(); // Kaller @PostConstruct manuelt siden vi kjører testen uten spring
        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse());
        when(unleashService.isEnabled(anyString())).thenReturn(true);
        when(unleashService.isEnabled(any(Feature.class))).thenReturn(true);
        MDC.put(MDCConstants.MDC_CALL_ID, "12345");

    }

    @Test
    void transformererResponseTilSaksliste() {
        when(sakApiGateway.hentSaker(anyString())).thenReturn(SakDataGenerator.Companion.createSaksliste());
        List<Sak> saksliste = sakerService.hentSammensatteSakerResultat(FNR).getSaker();
        assertThat(saksliste.get(0).saksId, is(SakId_1));
        assertThat(saksliste.get(3).fagsystemKode, is(""));
        assertThat(saksliste.get(saksliste.size() - 1).sakstype, is(SAKSTYPE_MED_FAGSAK));
        assertThat(saksliste.get(saksliste.size() - 1).temaKode, is(BIDRAG_MARKOR));
        assertThat(saksliste.get(saksliste.size() - 1).temaNavn, is("Bidrag"));
        assertThat(saksliste.get(saksliste.size() - 1).fagsystemNavn, is("Kopiert inn i Bisys"));
    }

    @Test
    void transformererResponseTilSakslistePensjon() {
        Sak pensjon = new Sak();
        pensjon.temaKode = "PENS";
        Sak ufore = new Sak();
        ufore.temaKode = "UFO";
        List<Sak> pensjonssaker = asList(pensjon, ufore);
        when(psakService.hentSakerFor(FNR)).thenReturn(pensjonssaker);
        List<Sak> saksliste = sakerService.hentPensjonSaker(FNR);

        assertThat(saksliste.size(), is(2));
        assertThat(saksliste.get(0).temaNavn, is("PENS"));
        assertThat(saksliste.get(1).temaNavn, is("UFO"));
    }

    @Test
    void oppretterIkkeGenerellOppfolgingssakOgfjernerGenerellOppfolgingssakDersomFagsakerInneholderOppfolgingssak() {

        when(sakApiGateway.hentSaker(anyString())).thenReturn(SakDataGenerator.Companion.createOppfolgingSaksliste());
        List<Sak> saker = sakerService.hentSammensatteSakerResultat(FNR).getSaker().stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect(toList());

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).sakstype, not(is(SAKSTYPE_GENERELL)));
    }

    @Test
    void oppretterIkkeGenerellOppfolgingssakDersomDenneFinnesAlleredeSelvOmFagsakerIkkeInneholderOppfolgingssak() {
        List<Sak> saker = sakerService.hentSaker(FNR).getSaker().stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect(toList());
        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).sakstype, is(SAKSTYPE_GENERELL));
    }

    @Test
    void leggerTilOppfolgingssakFraArenaDersomDenneIkkeFinnesIGsak() {
        String saksId = "123456";
        LocalDate dato = LocalDate.now().minusDays(1);

        when(arbeidOgAktivitet.hentSakListe(any(WSHentSakListeRequest.class))).thenReturn(new WSHentSakListeResponse().withSakListe(
                new no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak()
                        .withFagomradeKode(new Fagomradekode().withKode(TEMAKODE_OPPFOLGING))
                        .withSaksId(saksId)
                        .withEndringsInfo(new EndringsInfo().withOpprettetDato(dato))
                        .withSakstypeKode(new Sakstypekode().withKode("ARBEID"))
        ));

        List<Sak> saker = sakerService.hentSammensatteSaker(FNR).stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).collect(toList());

        assertThat(saker.size(), is(1));
        assertThat(saker.get(0).getSaksIdVisning(), is(saksId));
        assertThat(saker.get(0).opprettetDato, is(dato.toDateTimeAtStartOfDay()));
        assertThat(saker.get(0).fagsystemKode, is(FAGSYSTEMKODE_ARENA));
        assertThat(saker.get(0).finnesIGsak, is(false));
    }

}
