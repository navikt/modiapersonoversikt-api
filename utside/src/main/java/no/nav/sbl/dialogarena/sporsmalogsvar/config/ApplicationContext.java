package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.cache.CacheConfig;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.WicketApplication;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(CacheConfig.class)
public class ApplicationContext {
	
	@Bean
	public ContentRetriever contentRetriever() {
		// Egen bønne for å hooke opp @Cachable
		return new HttpContentRetriever();
	}

	@Bean
	public CmsContentRetriever cmsContentRetriever(ContentRetriever contentRetriever) throws URISyntaxException {
		String cmsBaseUrl = System.getProperty("dialogarena.cms.url");
		Map<String, URI> uris = new HashMap<>();
		uris.put("nb", new URI(cmsBaseUrl + "/site/16/sbl-webkomponenter/nb/tekster"));
		ValueRetriever valueRetriever = new ValuesFromContentWithResourceBundleFallback("content.sbl-webkomponenter", contentRetriever, uris, "nb");
		CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
		cmsContentRetriever.setDefaultLocale("nb");
		cmsContentRetriever.setTeksterRetriever(valueRetriever);
		return cmsContentRetriever;
	}

	@Bean
	public WicketApplication wicket() {
		return new WicketApplication();
	}

	@Bean
	public SporsmalOgSvarPortType sporsmalOgSvarPortType() {
		return createPortType(System.getProperty("henvendelser.webservice.sporsmal.url"), "classpath:SporsmalOgSvar.wsdl",
				SporsmalOgSvarPortType.class);
	}

	@Bean
	public HenvendelsePortType henvendelsePortType() {
		return createPortType(System.getProperty("henvendelser.webservice.felles.url"), "classpath:Henvendelse.wsdl",
				HenvendelsePortType.class);
	}

	@Bean
	public MeldingService meldingService(HenvendelsePortType hpt, SporsmalOgSvarPortType sospt) {
		return new MeldingService.Default(hpt, sospt);
	}

	private static <T> T createPortType(String address, String wsdlUrl, Class<T> serviceClass) {
		JaxWsProxyFactoryBean proxy = new JaxWsProxyFactoryBean();
		proxy.getFeatures().add(new WSAddressingFeature());
		proxy.getFeatures().add(new LoggingFeature());
		proxy.setServiceClass(serviceClass);
		proxy.setAddress(address);
		proxy.setWsdlURL(wsdlUrl);
		proxy.setProperties(new HashMap<String, Object>());
		proxy.getProperties().put(SecurityConstants.MUSTUNDERSTAND, false);

		T portType = proxy.create(serviceClass);
		Client client = ClientProxy.getClient(portType);
		HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
		httpConduit.setTlsClientParameters(new TLSClientParameters());
		if (Boolean.valueOf(System.getProperty("disable.ssl.cn.check", "false"))) {
			httpConduit.getTlsClientParameters().setDisableCNCheck(true);
		}
		STSConfigurationUtility.configureStsForExternalSSO(client);
		return portType;
	}

}
