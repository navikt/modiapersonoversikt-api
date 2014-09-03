package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.SAKSTYPE_GENERELL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.TestUtils.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HenvendelseBehandlingServiceTest {

    private static final String FNR = "11111111";
    private static final String TEMAGRUPPE = "temagruppe";
    private static final String BEHANDLINGS_ID = "id1";
    private static final String SAKS_ID = "111111111";
    private static final String SAKSTEMA = "tema";
    private static final String SAKSTYPE = "Fagsystem1";
    private static final String JOURNALPOST_ID = "journalpostId";
    private static final String NAVIDENT = "navident";

    @Captor
    private ArgumentCaptor<WSHentHenvendelseListeRequest> wsHentHenvendelseListeRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<XMLJournalfortInformasjon> xmlJournalfortInformasjonArgumentCaptor;

    @Mock
    private HenvendelsePortType henvendelsePortType;
    @Mock
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Mock
    private EnforcementPoint pep;
    @Mock
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    @InjectMocks
    private HenvendelseBehandlingService henvendelseBehandlingService;

    private XMLHenvendelse xmlHenvendelse;
    private Sak sak;
    private Melding melding;

    @Before
    public void setUp() {
        XMLMeldingFraBruker xmlMeldingFraBruker = new XMLMeldingFraBruker()
                .withFritekst("fritekst")
                .withTemagruppe(TEMAGRUPPE);

        List<Object> xmlHenvendelseListe = new ArrayList<>();
        xmlHenvendelse = lagXMLHenvendelse(BEHANDLINGS_ID, DateTime.now(), XMLHenvendelseType.SPORSMAL.name(), xmlMeldingFraBruker);
        xmlHenvendelseListe.add(xmlHenvendelse);

        when(henvendelsePortType.hentHenvendelseListe(any(WSHentHenvendelseListeRequest.class))).thenReturn(
                new WSHentHenvendelseListeResponse().withAny(xmlHenvendelseListe));

        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("1231");

        sak = TestUtils.createSak(SAKS_ID, SAKSTEMA, SAKSTYPE, SAKSTYPE_GENERELL, DateTime.now().minusDays(4));
        melding = createMelding(BEHANDLINGS_ID, Meldingstype.SPORSMAL, DateTime.now(), TEMAGRUPPE, BEHANDLINGS_ID);
    }

    @Test
    public void skalHenteMeldingerMedRiktigType() {
        henvendelseBehandlingService.hentMeldinger(FNR);

        verify(henvendelsePortType).hentHenvendelseListe(wsHentHenvendelseListeRequestArgumentCaptor.capture());
        WSHentHenvendelseListeRequest request = wsHentHenvendelseListeRequestArgumentCaptor.getValue();

        assertThat(request.getFodselsnummer(), is(FNR));
        assertTrue(request.getTyper().contains(SPORSMAL.name()));
        assertTrue(request.getTyper().contains(SVAR.name()));
        assertTrue(request.getTyper().contains(REFERAT.name()));
    }

    @Test
    public void skalTransformereResponsenTilMeldingsliste() {
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(FNR);

        assertThat(meldinger.size(), is(1));
        assertThat(meldinger.get(0).id, is(BEHANDLINGS_ID));
    }

    @Test
    public void skalSendeJournalfortInformasjonTilBehandleHenvendelse() {
        innloggetBrukerEr(NAVIDENT);
        henvendelseBehandlingService.oppdaterJournalfortInformasjonIHenvendelse(sak, JOURNALPOST_ID, melding);

        verify(behandleHenvendelsePortType).oppdaterJournalfortInformasjon(anyString(), xmlJournalfortInformasjonArgumentCaptor.capture());
        XMLJournalfortInformasjon journalfortInformasjon = xmlJournalfortInformasjonArgumentCaptor.getValue();

        assertThat(journalfortInformasjon.getJournalfortTema(), is(SAKSTEMA));
        assertThat(journalfortInformasjon.getJournalpostId(), is(JOURNALPOST_ID));
        assertThat(journalfortInformasjon.getJournalfortSaksId(), is(SAKS_ID));
        assertThat(journalfortInformasjon.getJournalforerNavIdent(), is(NAVIDENT));
    }

}