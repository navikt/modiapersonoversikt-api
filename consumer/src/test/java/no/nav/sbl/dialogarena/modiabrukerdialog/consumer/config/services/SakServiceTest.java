package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmaal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HenvendelseTestConfig.class, OppgaveTestConfig.class})
public class SakServiceTest {

    private final static String FNR = "fnr";
    private final static String SPORSMAL_ID = "id";
    private final static String FRITEKST = "fritekst";
    private final static String TEMAGRUPPE = "temagruppe";

    @Inject
    private SakService sakService;

    @Inject
    private HenvendelsePortType henvendelsePortType;

    @Inject
    protected SendHenvendelsePortType sendHenvendelsePortType;

    @Captor
    ArgumentCaptor<WSSendHenvendelseRequest> captor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void skalHenteSporsmaal() {
        when(henvendelsePortType.hentHenvendelse(any(WSHentHenvendelseRequest.class))).thenReturn(mockWSHentHenvendelseResponse());

        Sporsmaal sporsmaal = sakService.getSporsmaal(SPORSMAL_ID);

        assertThat(sporsmaal.id, is(SPORSMAL_ID));
        assertThat(sporsmaal.fritekst, is(FRITEKST));
        assertThat(sporsmaal.tema, is(TEMAGRUPPE));
    }

    @Test
    public void skalSendeReferat() {
        sakService.sendReferat(new Referat().withFnr(FNR).withFritekst(FRITEKST));

        verify(sendHenvendelsePortType).sendHenvendelse(captor.capture());
        assertThat(captor.getValue().getType(), is(REFERAT.name()));
    }

    @Test
    public void skalSendeSvar() {
        sakService.sendSvar(new Svar().withFnr(FNR).withFritekst(FRITEKST));

        verify(sendHenvendelsePortType).sendHenvendelse(captor.capture());
        assertThat(captor.getValue().getType(), is(SVAR.name()));
    }

    private WSHentHenvendelseResponse mockWSHentHenvendelseResponse() {
        return new WSHentHenvendelseResponse().withAny(
                new XMLBehandlingsinformasjon()
                        .withBehandlingsId(SPORSMAL_ID)
                        .withOpprettetDato(DateTime.now())
                        .withHenvendelseType(SPORSMAL.name())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLSporsmal().withFritekst(FRITEKST).withTemagruppe(TEMAGRUPPE))));
    }

}
