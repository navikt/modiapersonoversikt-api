package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk.KodeverkV2EndpointConfig.KODEVERK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.ALLOW_MOCK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        KodeverkWrapperTestConfig.class,
        KodeverkV2EndpointConfig.class
})
public class KodeverkConfigTest {

    @Inject
    private KodeverkPortType portType;

    @Inject
    @Qualifier("kodeverkPort")
    private Wrapper<KodeverkV2PortTypeImpl> kodeverkPort;

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setProperty(KODEVERK_KEY, ALLOW_MOCK);
        portType.finnKodeverkListe(new XMLFinnKodeverkListeRequest());
        portType.hentKodeverk(new XMLHentKodeverkRequest());
        portType.ping();
        verify(kodeverkPort.wrappedObject, times(1)).kodeverkPortType();//Oppstart av switcher
    }

}
