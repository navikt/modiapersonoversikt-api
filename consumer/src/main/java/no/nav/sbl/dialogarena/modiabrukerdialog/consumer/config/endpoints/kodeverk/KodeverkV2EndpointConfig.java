package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.KodeverkV2PortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.XMLKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.jws.WebParam;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class KodeverkV2EndpointConfig {

    public static final String KODEVERK_KEY = "start.kodeverk.withmock";

    @Inject
    @Qualifier("kodeverkPort")
    private Wrapper<KodeverkV2PortTypeImpl> kodeverkPort;

    @Inject
    @Qualifier("kodeverkMock")
    private Wrapper<KodeverkV2PortTypeImpl> kodeverkMock;

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        return new KodeverkPortType() {

            @Cacheable("kodeverkCache")
            @Override
            public XMLHentKodeverkResponse hentKodeverk(@WebParam(name = "request", targetNamespace = "") XMLHentKodeverkRequest xmlHentKodeverkRequest)
                    throws HentKodeverkHentKodeverkKodeverkIkkeFunnet {
                if (mockErTillattOgSlaattPaaForKey(KODEVERK_KEY)) {
                    return kodeverkMock.wrappedObject.kodeverkPortType().hentKodeverk(xmlHentKodeverkRequest);
                }
                return kodeverkPort.wrappedObject.kodeverkPortType().hentKodeverk(xmlHentKodeverkRequest);
            }

            @Cacheable("kodeverkCache")
            @Override
            public XMLFinnKodeverkListeResponse finnKodeverkListe(@WebParam(name = "request", targetNamespace = "") XMLFinnKodeverkListeRequest xmlFinnKodeverkListeRequest) {
                if (mockErTillattOgSlaattPaaForKey(KODEVERK_KEY)) {
                    return kodeverkMock.wrappedObject.kodeverkPortType().finnKodeverkListe(xmlFinnKodeverkListeRequest);
                }
                return kodeverkPort.wrappedObject.kodeverkPortType().finnKodeverkListe(xmlFinnKodeverkListeRequest);
            }

            @Override
            public void ping() {
                if (mockErTillattOgSlaattPaaForKey(KODEVERK_KEY)) {
                    kodeverkMock.wrappedObject.kodeverkPortType().ping();
                    return;
                }
                kodeverkPort.wrappedObject.kodeverkPortType().ping();
                return;
            }
        };
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        return new KodeverkClient() {

            @Cacheable("kodeverkCache")
            @Override
            public XMLKodeverk hentKodeverk(String s) {
                if (mockErTillattOgSlaattPaaForKey(KODEVERK_KEY)) {
                    return kodeverkMock.wrappedObject.kodeverkClient().hentKodeverk(s);
                }
                return kodeverkPort.wrappedObject.kodeverkClient().hentKodeverk(s);
            }

            @Cacheable("kodeverkCache")
            @Override
            public String hentFoersteTermnavnForKode(String s, String s2) {
                if (mockErTillattOgSlaattPaaForKey(KODEVERK_KEY)) {
                    return kodeverkMock.wrappedObject.kodeverkClient().hentFoersteTermnavnForKode(s, s2);
                }
                return kodeverkPort.wrappedObject.kodeverkClient().hentFoersteTermnavnForKode(s, s2);
            }
        };
    }
}
