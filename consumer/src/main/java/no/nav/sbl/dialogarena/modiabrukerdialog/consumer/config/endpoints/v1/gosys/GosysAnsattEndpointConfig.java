package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.v1.gosys;

import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GosysNavAnsattPortTypeMock.createGosysNavAnsattPortTypeMock;

@Configuration
public class GosysAnsattEndpointConfig {

    public static final String GOSYS_ANSATT_KEY = "start.gosys.ansatt.withmock";

    @Bean
    public GOSYSNAVansatt gosysNavAnsatt() {
        return createSwitcher(createGosysNavAnsattPortType(), createGosysNavAnsattPortTypeMock(), GOSYS_ANSATT_KEY, GOSYSNAVansatt.class);
    }

    private static GOSYSNAVansatt createGosysNavAnsattPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();

        proxyFactoryBean.setWsdlLocation("classpath:nav-cons-sak-gosys-3.0.0_GOSYSNAVAnsattWSEXP.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("tjenestebuss.url") + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVAnsattWSEXP");
        proxyFactoryBean.setServiceName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVansatt/Binding", "GOSYSNAVAnsattWSEXP_GOSYSNAVansattHttpService"));
        proxyFactoryBean.setEndpointName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVansatt/Binding", "GOSYSNAVAnsattWSEXP_GOSYSNAVansattHttpPort"));
        proxyFactoryBean.setServiceClass(GOSYSNAVansatt.class);
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        proxyFactoryBean.getOutInterceptors().add(new WSS4JOutInterceptor(getSecurityProps()));

        GOSYSNAVansatt gosysnaVansatt = proxyFactoryBean.create(GOSYSNAVansatt.class);

        HTTPConduit httpConduit = (HTTPConduit) ClientProxy.getClient(gosysnaVansatt).getConduit();
        TLSClientParameters params = new TLSClientParameters();
        params.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(params);

        return gosysnaVansatt;
    }


    private static Map<String, Object> getSecurityProps() {
        String user = System.getProperty("ctjenestebuss.username");

        Map<String, Object> props = new HashMap<>();
        props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        props.put(WSHandlerConstants.USER, user);
        props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        props.put(WSHandlerConstants.PW_CALLBACK_REF, new CallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                String password = System.getProperty("ctjenestebuss.password");

                WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[0];
                passwordCallback.setPassword(password);
            }
        });
        return props;
    }
}
