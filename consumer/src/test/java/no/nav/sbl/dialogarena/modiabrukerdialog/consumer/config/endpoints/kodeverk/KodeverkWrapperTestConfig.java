package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.KodeverkV2PortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class KodeverkWrapperTestConfig {

    @Bean
    @Qualifier("kodeverkPort")
    public Wrapper<KodeverkV2PortTypeImpl> kodeverkPort() {
        KodeverkV2PortTypeImpl mock = mock(KodeverkV2PortTypeImpl.class);
        KodeverkPortType portTypeMock = mock(KodeverkPortType.class);
        try {
            when(portTypeMock.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenReturn(new XMLHentKodeverkResponse());
        } catch (HentKodeverkHentKodeverkKodeverkIkkeFunnet hentKodeverkHentKodeverkKodeverkIkkeFunnet) {
            //mock, vi styrer oppførsel
        }
        when(mock.kodeverkPortType()).thenReturn(portTypeMock);
        return new Wrapper<>(mock);
    }

    @Bean
    @Qualifier("kodeverkMock")
    public Wrapper<KodeverkV2PortTypeImpl> kodeverkMock() {
        KodeverkV2PortTypeImpl mock = mock(KodeverkV2PortTypeImpl.class);
        KodeverkPortType portTypeMock = mock(KodeverkPortType.class);
        try {
            when(portTypeMock.hentKodeverk(any(XMLHentKodeverkRequest.class))).thenReturn(new XMLHentKodeverkResponse());
        } catch (HentKodeverkHentKodeverkKodeverkIkkeFunnet hentKodeverkHentKodeverkKodeverkIkkeFunnet) {
            //mock, vi styrer oppførsel
        }
        when(mock.kodeverkPortType()).thenReturn(portTypeMock);
        return new Wrapper<>(mock);
    }

}
