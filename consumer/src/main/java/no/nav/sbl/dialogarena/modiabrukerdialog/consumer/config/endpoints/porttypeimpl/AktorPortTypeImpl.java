package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.integrasjon.features.TimeoutFeature;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentResponse;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_CONNECTION_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_RECEIVE_TIMEOUT;

public class AktorPortTypeImpl {

    private URL aktorEndpoint;

    public AktoerPortType aktorPortType() {
//        return createAktorIdPortType(new UserSAMLOutInterceptor());
        return new AktoerPortType() {

            private Map<String, String> aktorIdMap = new HashMap<>();

            {
                aktorIdMap.put("01010091736", "69078469165827");
                aktorIdMap.put("06047848871", "29078469165474");
                aktorIdMap.put("23054549733", "79078469165571");
                aktorIdMap.put("15066849497", "19078469165809");
                aktorIdMap.put("06025800174", "69078469165205");
                aktorIdMap.put("01010090195", "Ukjent");
            }

            @Override
            public HentAktoerIdForIdentResponse hentAktoerIdForIdent(HentAktoerIdForIdentRequest hentAktoerIdForIdentRequest) throws HentAktoerIdForIdentPersonIkkeFunnet {
                HentAktoerIdForIdentResponse response = new HentAktoerIdForIdentResponse();
                if (aktorIdMap.containsKey(hentAktoerIdForIdentRequest.getIdent())) {
                    response.setAktoerId(aktorIdMap.get(hentAktoerIdForIdentRequest.getIdent()));
                } else {
                    //default
                    response.setAktoerId("29078469165474");
                }
                return response;

            }

            @Override
            public void ping() {
            }
        };
    }

//    @Bean
//    public Pingable aktorIdPing() {
//        final AktoerPortType aktorIdPortType = createAktorIdPortType(new SystemSAMLOutInterceptor());
//        return new Pingable() {
//            @Override
//            public List<PingResult> ping() {
//                PingResult.ServiceResult result;
//                long start = currentTimeMillis();
//                try {
//                    aktorIdPortType.ping();
//                    result = SERVICE_OK;
//                } catch (Exception e) {
//                    result = SERVICE_FAIL;
//                }
//                return asList(new PingResult("Aktoer_v1", result, currentTimeMillis() - start));
//            }
//        };
//    }

    public Pingable aktorIdPing() {

//        final AktoerPortType aktorIdPortType = createAktorIdPortType(new SystemSAMLOutInterceptor());
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                PingResult.ServiceResult result;
                long start = currentTimeMillis();
                try {
//                    aktorIdPortType.ping();
                    result = SERVICE_OK;
                } catch (Exception e) {
                    result = SERVICE_FAIL;
                }
                return asList(new PingResult("Aktoer_v1", result, currentTimeMillis() - start));
            }
        };
    }

    private AktoerPortType createAktorIdPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("wsdl/no/nav/tjeneste/virksomhet/aktoer/v1/Aktoer.wsdl");
        proxyFactoryBean.setAddress(aktorEndpoint.toString());
        proxyFactoryBean.setServiceClass(AktoerPortType.class);
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.getFeatures().add(new TimeoutFeature().withConnectionTimeout(MODIA_CONNECTION_TIMEOUT).withReceiveTimeout(MODIA_RECEIVE_TIMEOUT));
        return proxyFactoryBean.create(AktoerPortType.class);
    }
}
