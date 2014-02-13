package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.KodeverkV2PortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk.KodeverkV2EndpointConfig.KODEVERK_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.ALLOW_MOCK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.mockito.Mockito.verifyZeroInteractions;

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
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() {
        setProperty(TILLATMOCKSETUP_PROPERTY, "http://ja.nav.no");
        setProperty(KODEVERK_KEY, ALLOW_MOCK);
        portType.finnKodeverkListe(new XMLFinnKodeverkListeRequest());
        verifyZeroInteractions(kodeverkPort.wrappedObject);
    }

//    @Test
//    public void perDefaultSkalProdkodeEksekveres() {
//        setProperty(TILLATMOCKSETUP_PROPERTY, "nei");
//        portType.finnKodeverkListe(new XMLFinnKodeverkListeRequest());
//        verify(kodeverkPort.wrappedObject, times(1)).kodeverkPortType().finnKodeverkListe(any(XMLFinnKodeverkListeRequest.class));
//    }

}
