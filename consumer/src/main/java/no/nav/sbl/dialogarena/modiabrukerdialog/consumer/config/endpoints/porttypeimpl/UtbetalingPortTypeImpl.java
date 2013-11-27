package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl;


import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeRequest;
import no.nav.virksomhet.tjenester.utbetaling.meldinger.v2.WSHentUtbetalingListeResponse;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeBaksystemIkkeTilgjengelig;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeForMangeForekomster;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeMottakerIkkeFunnet;
import no.nav.virksomhet.tjenester.utbetaling.v2.HentUtbetalingListeUgyldigDato;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.cache.annotation.Cacheable;

/**
 * Fake endpoint, tjeneste eksisterer ikke pt
 */
public class UtbetalingPortTypeImpl {

    private String utbetalingEndpoint = "https://modapp-t11.adeo.no/utbetaling";

    public no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling utbetalingPortType() {
        return new no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling() {
            @Override
            public WSHentUtbetalingListeResponse hentUtbetalingListe(WSHentUtbetalingListeRequest request) {
                return new WSHentUtbetalingListeResponse();
            }
        };
//        return createUtbetalingPortType(new UserSAMLOutInterceptor());
    }

    @SuppressWarnings("unused")
    private no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling createUtbetalingPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("utbetaling/no/nav/virksomhet/tjenester/utbetaling/utbetaling.wsdl");
        proxyFactoryBean.setAddress(utbetalingEndpoint);
        proxyFactoryBean.setServiceClass(no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        final no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling utbetalingTjeneste = proxyFactoryBean.create(no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling.class);
        return new no.nav.virksomhet.tjenester.utbetaling.v2.Utbetaling() {

            @Cacheable("endpointCache")
            @Override
            public WSHentUtbetalingListeResponse hentUtbetalingListe(WSHentUtbetalingListeRequest request) throws HentUtbetalingListeMottakerIkkeFunnet, HentUtbetalingListeForMangeForekomster, HentUtbetalingListeBaksystemIkkeTilgjengelig, HentUtbetalingListeUgyldigDato {
                return utbetalingTjeneste.hentUtbetalingListe(request);
            }
        };

    }
}
