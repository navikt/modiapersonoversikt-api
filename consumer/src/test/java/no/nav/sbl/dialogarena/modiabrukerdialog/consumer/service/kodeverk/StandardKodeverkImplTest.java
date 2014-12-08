package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverk;

import no.nav.modig.core.exception.SystemException;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLEnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLPeriode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLTerm;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

import static no.nav.modig.core.test.FilesAndDirs.BUILD_OUTPUT;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StandardKodeverkImplTest {

    private final File dumpDir = new File(BUILD_OUTPUT, "kodeverkdump/" + randomNumeric(10));
    public static final String ARKIVTEMA_KODEVERKNAVN = "Arkivtemaer";

    @Mock
    private KodeverkPortType kodeverkPortType;

    @InjectMocks
    private StandardKodeverkImpl kodeverk;

    @Before
    public void wireUpKodeverk() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        ReflectionTestUtils.setField(kodeverk, "brukerprofilDataDirectory", dumpDir);
        kodeverk.initKodeverk();
    }

    @Test
    public void slaarOppArkivtemaNavnBasertPaaArkivtemaKode() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        when(kodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenReturn(arkivtemaKodeverkResponse());
        assertThat(kodeverk.getArkivtemaNavn("DAG"), is("Dagpenger"));
        assertThat(kodeverk.getArkivtemaNavn("BID"), is("Bidrag"));
    }

    @Test(expected = SystemException.class)
    public void ugyldigKodeverknavnGirSystemException() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        when(kodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenThrow(new HentKodeverkHentKodeverkKodeverkIkkeFunnet());
        kodeverk.lastInnNyeKodeverk();
    }

    @Test
    public void dumperInnlastetKodeverkTilFileOgBrukerDenneVedRestartDaKodeverkErNede() throws Exception {
        when(kodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenReturn(arkivtemaKodeverkResponse());
        kodeverk.lastInnNyeKodeverk();

        when(kodeverkPortType.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenThrow(new RuntimeException("Kodeverk er nede"));
        kodeverk.lastInnNyeKodeverk();
        wireUpKodeverk();
        kodeverk.lastInnNyeKodeverk();
    }

    private static XMLHentKodeverkResponse arkivtemaKodeverkResponse() {
        XMLKode dagpenger = new XMLKode().withNavn("DAG").withTerm(new XMLTerm().withNavn("Dagpenger")).withGyldighetsperiode(new XMLPeriode().withFom(DateMidnight.now().minusDays(1)).withTom(DateMidnight.now().plusDays(1)));
        XMLKode bidrag = new XMLKode().withNavn("BID").withTerm(new XMLTerm().withNavn("Bidrag")).withGyldighetsperiode(new XMLPeriode().withFom(DateMidnight.now().minusDays(1)).withTom(DateMidnight.now().plusDays(1)));

        return new XMLHentKodeverkResponse().withKodeverk(
                new XMLEnkeltKodeverk().withNavn(ARKIVTEMA_KODEVERKNAVN).withKode(dagpenger, bidrag));
    }

}
