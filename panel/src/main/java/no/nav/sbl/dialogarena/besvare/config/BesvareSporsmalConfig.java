package no.nav.sbl.dialogarena.besvare.config;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.HenvendelseSporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.BesvarSporsmalRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.BesvarSporsmalResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentAlleSporsmalOgSvarRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentAlleSporsmalOgSvarResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentSporsmalOgSvarRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.HentSporsmalOgSvarResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.OpprettSporsmalRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.OpprettSporsmalResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.SporsmalOgSvar;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(JaxWsFeatures.Integration.class)
public class BesvareSporsmalConfig {

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Value("${spormalogsvarendpoint.url}")
    protected String spmSvarEndpoint;

    @Bean
    public HenvendelseSporsmalOgSvarPortType sporsmalOgSvarPortType() {
//        return henvendelseSporsmalOgSvarPortTypeFactory().create(HenvendelseSporsmalOgSvarPortType.class);
        return new HenvendelseSporsmalOgSvarPortType() {

            @Override
            public OpprettSporsmalResponse opprettSporsmal(OpprettSporsmalRequest parameters) {
                return null;
            }


            @Override
            public HentSporsmalOgSvarResponse hentSporsmalOgSvar(HentSporsmalOgSvarRequest parameters) {
                return null;
            }


            @Override
            public HentAlleSporsmalOgSvarResponse hentAlleSporsmalOgSvar(HentAlleSporsmalOgSvarRequest parameters) {
                return new HentAlleSporsmalOgSvarResponse().withSporsmalOgSvar(
                        new SporsmalOgSvar().withSporsmal("Kor e pængan?"),
                        new SporsmalOgSvar().withSporsmal("Kor e dæm henne? I sjarken?"));
            }


            @Override
            public BesvarSporsmalResponse besvarSporsmal(BesvarSporsmalRequest parameters) {
                return null;
            }
        };
    }

    @Bean
    public JaxWsProxyFactoryBean henvendelseSporsmalOgSvarPortTypeFactory() {
        JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig();
        jaxwsClient.setServiceClass(HenvendelseSporsmalOgSvarPortType.class);
        jaxwsClient.setAddress(spmSvarEndpoint);
        jaxwsClient.setWsdlURL(classpathUrl("HenvendelseSporsmalOgSvar.wsdl"));
        return jaxwsClient;
    }

    @Bean
    public JaxWsProxyFactoryBean commonJaxWsConfig() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        factoryBean.setProperties(properties);
        factoryBean.getFeatures().addAll(jaxwsFeatures.jaxwsFeatures());
        return factoryBean;
    }

    private String classpathUrl(String classpathLocation) {
        if (getClass().getClassLoader().getResource(classpathLocation) == null) {
            throw new RuntimeException(classpathLocation + " does not exist on classpath!");
        }
        return "classpath:" + classpathLocation;
    }

}