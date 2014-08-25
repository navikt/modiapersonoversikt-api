package no.nav.sbl.modiabrukerdialog.pip.geografisk.config;

import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.GOSYSNAVOrgEnhet;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.consumer.GOSYSNAVansatt;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.DefaultEnhetAttributeLocatorDelegate;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.EnhetAttributeLocatorDelegate;
import org.apache.commons.lang3.Validate;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
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

/**
 * Spring configuration for NAVOrgEnhet and NAVAnsatt.
 */
@Configuration
public class GeografiskPipConfig {

	public static final String TJENESTEBUSS_URL_KEY = "tjenestebuss.url";
	public static final String TJENESTEBUSS_USERNAME_KEY = "ctjenestebuss.username";
	public static final String TJENESTEBUSS_PASSWORD_KEY = "ctjenestebuss.password";


	@Bean
	public EnhetAttributeLocatorDelegate enhetAttributeLocatorDelegate() {
		return new DefaultEnhetAttributeLocatorDelegate(navAnsatt(), navOrgEnhet());
	}

	@Bean
	public GOSYSNAVOrgEnhet navOrgEnhet() {
		JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();

		factoryBean.setWsdlURL("classpath:nav-cons-sak-gosys-3.0.0_GOSYSNAVOrgEnhetWSEXP.wsdl");
		factoryBean.setServiceName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVOrgEnhet/Binding", "GOSYSNAVOrgEnhetWSEXP_GOSYSNAVOrgEnhetHttpService"));
		factoryBean.setEndpointName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVOrgEnhet/Binding", "GOSYSNAVOrgEnhetWSEXP_GOSYSNAVOrgEnhetHttpPort"));
		factoryBean.setAddress(getAdress() + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVOrgEnhetWSEXP");
		factoryBean.setServiceClass(GOSYSNAVOrgEnhet.class);
		factoryBean.getFeatures().add(new WSAddressingFeature());
		factoryBean.getOutInterceptors().add(new WSS4JOutInterceptor(getSecurityProps()));

		GOSYSNAVOrgEnhet port = factoryBean.create(GOSYSNAVOrgEnhet.class);

		return port;
	}

	@Bean
	public GOSYSNAVansatt navAnsatt() {
		JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();

		factoryBean.setWsdlURL("classpath:nav-cons-sak-gosys-3.0.0_GOSYSNAVAnsattWSEXP.wsdl");
		factoryBean.setServiceName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVansatt/Binding", "GOSYSNAVAnsattWSEXP_GOSYSNAVansattHttpService"));
		factoryBean.setEndpointName(new QName("http://nav-cons-sak-gosys-3.0.0/no/nav/inf/NAVansatt/Binding", "GOSYSNAVAnsattWSEXP_GOSYSNAVansattHttpPort"));
		factoryBean.setAddress(getAdress() + "nav-cons-sak-gosys-3.0.0Web/sca/GOSYSNAVAnsattWSEXP");
		factoryBean.setServiceClass(GOSYSNAVansatt.class);
		factoryBean.getFeatures().add(new WSAddressingFeature());
		factoryBean.getOutInterceptors().add(new WSS4JOutInterceptor(getSecurityProps()));

		GOSYSNAVansatt port = factoryBean.create(GOSYSNAVansatt.class);

		return port;
	}

	private String getAdress() {
		String tjenestebussUrl = System.getProperty(TJENESTEBUSS_URL_KEY);
		Validate.notBlank(tjenestebussUrl, "URL for tjenestebuss cannot be empty. " + TJENESTEBUSS_URL_KEY + " property must exist.");
		return tjenestebussUrl;
	}

	private Map<String, Object> getSecurityProps() {
		String user = System.getProperty(TJENESTEBUSS_USERNAME_KEY);
		Validate.notBlank("System user for tjenestebuss must be set. Property key: " + TJENESTEBUSS_USERNAME_KEY);

		Map<String, Object> props = new HashMap<>();
		props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
		props.put(WSHandlerConstants.USER, user);
		props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
		props.put(WSHandlerConstants.PW_CALLBACK_REF, new CallbackHandler() {
			@Override
			public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
				String password = System.getProperty(TJENESTEBUSS_PASSWORD_KEY);
				Validate.notBlank("System user password for tjenestebuss must be set. Property key: " + TJENESTEBUSS_PASSWORD_KEY);

				WSPasswordCallback passwordCallback = (WSPasswordCallback) callbacks[0];
				passwordCallback.setPassword(password);
			}
		});
		return props;
	}
}

